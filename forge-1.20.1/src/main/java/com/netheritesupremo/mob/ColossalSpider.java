package com.netheritesupremo.mob;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import com.netheritesupremo.registry.ModEntities;

/**
 * Aranha colossal (mini boss): 450 de vida, 50 de dano. Poe teias no chao e em arvores; quem encosta numa
 * teia vira alvo dela. Tem medo de fogo e tochas (foge). Em combate atira teias, e agarra o alvo para dar
 * varios golpes.
 */
public class ColossalSpider extends Spider {
    public static final double MAX_HEALTH = 450.0D;
    public static final double DAMAGE = 50.0D;
    public static final int MAX_WEBS = 24;
    public static final int FEAR_RADIUS = 5;
    public static final int GRAB_HITS = 5;
    public static final int GRAB_HIT_INTERVAL = 12;
    public static final String GRABBED_TAG = "netheritesupremo_grabbed";

    private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(),
            BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.NOTCHED_10);
    private final List<BlockPos> webs = new ArrayList<>();
    @Nullable
    private Vec3 fearSource;
    private int fearSeen = -1000;
    private int webCooldown = 40;
    private int shotCooldown = 60;
    private int grabCooldown = 0;
    private int grabTicks = 0;
    private int grabHits = 0;
    @Nullable
    private UUID grabbedId;

    public ColossalSpider(EntityType<? extends ColossalSpider> type, Level level) {
        super(type, level);
        this.xpReward = 250;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Spider.createAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new FearGoal());
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.2F;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    // ------------------------------------------------------------------ teias
    public void trackWeb(BlockPos pos) {
        if (!this.webs.contains(pos)) {
            this.webs.add(pos.immutable());
        }
        while (this.webs.size() > MAX_WEBS + 8) {
            BlockPos old = this.webs.remove(0);
            if (this.level().getBlockState(old).is(Blocks.COBWEB)) {
                this.level().removeBlock(old, false);
            }
        }
    }

    public int webCount() {
        return this.webs.size();
    }

    private void placeRandomWeb() {
        if (!(this.level() instanceof ServerLevel level) || this.webs.size() >= MAX_WEBS) {
            return;
        }
        RandomSource r = this.random;
        int x = this.blockPosition().getX() + r.nextInt(21) - 10;
        int z = this.blockPosition().getZ() + r.nextInt(21) - 10;
        if (!level.hasChunkAt(new BlockPos(x, 0, z))) {
            return;
        }
        if (r.nextBoolean()) { // em arvores: ar encostado em folhas ou troncos
            int top = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
            for (int y = top; y > top - 12 && y > level.getMinBuildHeight(); y--) {
                BlockPos pos = new BlockPos(x, y, z);
                if (level.getBlockState(pos).isAir() && this.nextToTree(pos)) {
                    this.putWeb(level, pos);
                    return;
                }
            }
        } else { // no chao
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos pos = new BlockPos(x, y, z);
            if (level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) {
                this.putWeb(level, pos);
            }
        }
    }

    private boolean nextToTree(BlockPos pos) {
        for (Direction d : Direction.values()) {
            BlockState s = this.level().getBlockState(pos.relative(d));
            if (s.is(BlockTags.LEAVES) || s.is(BlockTags.LOGS)) {
                return true;
            }
        }
        return false;
    }

    private void putWeb(ServerLevel level, BlockPos pos) {
        level.setBlockAndUpdate(pos, Blocks.COBWEB.defaultBlockState());
        this.trackWeb(pos);
    }

    /** Quem encosta numa teia dela vira alvo. */
    private void checkWebTouch() {
        Iterator<BlockPos> it = this.webs.iterator();
        while (it.hasNext()) {
            BlockPos pos = it.next();
            if (!this.level().hasChunkAt(pos)) {
                continue;
            }
            if (!this.level().getBlockState(pos).is(Blocks.COBWEB)) {
                it.remove();
                continue;
            }
            List<LivingEntity> touching = this.level().getEntitiesOfClass(LivingEntity.class,
                    new AABB(pos).inflate(0.05D), this::isWebVictim);
            if (!touching.isEmpty()) {
                this.setTarget(touching.get(0));
                return;
            }
        }
    }

    private boolean isWebVictim(LivingEntity e) {
        if (e == this || !e.isAlive() || e instanceof Spider || e instanceof net.minecraft.world.entity.decoration.ArmorStand) {
            return false;
        }
        return !(e instanceof Player p) || (!p.isCreative() && !p.isSpectator());
    }

    // ------------------------------------------------------------------ medo
    private boolean scared() {
        return this.fearSource != null && this.tickCount - this.fearSeen <= 20;
    }

    private static boolean isFearBlock(BlockState s) {
        return s.is(Blocks.TORCH) || s.is(Blocks.WALL_TORCH) || s.is(Blocks.SOUL_TORCH) || s.is(Blocks.SOUL_WALL_TORCH)
                || s.is(Blocks.LANTERN) || s.is(Blocks.SOUL_LANTERN) || s.is(BlockTags.FIRE)
                || (s.is(BlockTags.CAMPFIRES) && s.hasProperty(net.minecraft.world.level.block.CampfireBlock.LIT)
                        && s.getValue(net.minecraft.world.level.block.CampfireBlock.LIT))
                || s.getFluidState().is(FluidTags.LAVA);
    }

    private static boolean isFearItem(ItemStack s) {
        return s.is(Items.TORCH) || s.is(Items.SOUL_TORCH) || s.is(Items.LANTERN) || s.is(Items.SOUL_LANTERN)
                || s.is(Items.FLINT_AND_STEEL) || s.is(Items.FIRE_CHARGE) || s.is(Items.LAVA_BUCKET);
    }

    private void scanFear() {
        BlockPos c = this.blockPosition();
        for (BlockPos p : BlockPos.betweenClosed(c.offset(-FEAR_RADIUS, -3, -FEAR_RADIUS), c.offset(FEAR_RADIUS, 3, FEAR_RADIUS))) {
            if (isFearBlock(this.level().getBlockState(p))) {
                this.fearSource = Vec3.atCenterOf(p);
                this.fearSeen = this.tickCount;
                return;
            }
        }
        for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(6.0D),
                x -> x != this && x.isAlive())) {
            if (e.isOnFire() || (e instanceof Player p && (isFearItem(p.getMainHandItem()) || isFearItem(p.getOffhandItem())))) {
                this.fearSource = e.position();
                this.fearSeen = this.tickCount;
                return;
            }
        }
    }

    private class FearGoal extends Goal {
        FearGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return ColossalSpider.this.scared();
        }

        @Override
        public boolean canContinueToUse() {
            return ColossalSpider.this.scared();
        }

        @Override
        public void start() {
            this.flee();
        }

        @Override
        public void tick() {
            if (ColossalSpider.this.getNavigation().isDone()) {
                this.flee();
            }
        }

        private void flee() {
            Vec3 away = DefaultRandomPos.getPosAway(ColossalSpider.this, 14, 7, ColossalSpider.this.fearSource);
            if (away != null) {
                ColossalSpider.this.getNavigation().moveTo(away.x, away.y, away.z, 1.5D);
            }
        }
    }

    // ------------------------------------------------------------------ combate
    private void shootWeb(LivingEntity target) {
        WebShot shot = new WebShot(ModEntities.WEB_SHOT.get(), this, this.level());
        double dx = target.getX() - shot.getX();
        double dy = target.getEyeY() - 0.8D - shot.getY();
        double dz = target.getZ() - shot.getZ();
        shot.shoot(dx, dy, dz, 1.3F, 3.0F);
        this.level().addFreshEntity(shot);
        this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.6F);
    }

    @Nullable
    private LivingEntity grabbed() {
        if (this.grabbedId != null && this.level() instanceof ServerLevel sl
                && sl.getEntity(this.grabbedId) instanceof LivingEntity e) {
            return e;
        }
        return null;
    }

    private void startGrab(LivingEntity target) {
        this.grabbedId = target.getUUID();
        this.grabTicks = 0;
        this.grabHits = 0;
        this.getNavigation().stop();
        this.playSound(SoundEvents.SPIDER_AMBIENT, 1.5F, 0.5F);
    }

    public void release(boolean throwAway) {
        LivingEntity g = this.grabbed();
        if (g != null && throwAway) {
            Vec3 look = this.getLookAngle();
            g.setDeltaMovement(look.x * 1.2D, 0.6D, look.z * 1.2D);
            g.hurtMarked = true;
        }
        this.grabbedId = null;
        this.grabCooldown = 160;
    }

    private void holdGrabbed(LivingEntity g) {
        Vec3 look = this.getLookAngle();
        double x = this.getX() + look.x * 2.0D;
        double y = this.getY() + 0.4D;
        double z = this.getZ() + look.z * 2.0D;
        if (g instanceof ServerPlayer sp) {
            sp.connection.teleport(x, y, z, sp.getYRot(), sp.getXRot());
        } else {
            g.moveTo(x, y, z, g.getYRot(), g.getXRot());
        }
        g.setDeltaMovement(Vec3.ZERO);
        g.fallDistance = 0.0F;
    }

    private void tickGrab(LivingEntity g) {
        if (!g.isAlive() || this.scared() || g.distanceToSqr(this) > 36.0D) {
            this.release(false);
            return;
        }
        if (this.tickCount % 2 == 0) {
            this.holdGrabbed(g);
        }
        this.grabTicks++;
        if (this.grabTicks % GRAB_HIT_INTERVAL == 0) {
            this.swing(InteractionHand.MAIN_HAND);
            g.invulnerableTime = 0;
            g.hurt(this.damageSources().mobAttack(this), (float) (DAMAGE * 0.4D));
            this.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1.0F, 0.7F);
            this.grabHits++;
            if (this.grabHits >= GRAB_HITS) {
                this.release(true);
            }
        }
    }

    // ------------------------------------------------------------------ tick
    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            return;
        }
        if (this.tickCount % 10 == 0) {
            this.scanFear();
        }
        if (this.grabCooldown > 0) {
            this.grabCooldown--;
        }
        LivingEntity grabbed = this.grabbed();
        if (this.grabbedId != null && grabbed == null) {
            this.grabbedId = null;
        }
        if (grabbed != null) {
            this.tickGrab(grabbed);
            return;
        }
        LivingEntity target = this.getTarget();
        if (target == null) {
            if (this.tickCount % 4 == 0) {
                this.checkWebTouch();
            }
            if (this.tickCount % 10 == 0) {
                Player p = this.level().getNearestPlayer(this, 6.0D);
                if (p != null && !p.isCreative() && !p.isSpectator() && this.hasLineOfSight(p)) {
                    this.setTarget(p);
                }
            }
            if (--this.webCooldown <= 0) {
                this.webCooldown = 40 + this.random.nextInt(40);
                this.placeRandomWeb();
            }
            return;
        }
        if (!target.isAlive() || target.distanceToSqr(this) > 32.0D * 32.0D) {
            this.setTarget(null);
            return;
        }
        if (this.scared()) {
            return;
        }
        double dist = this.distanceTo(target);
        if (this.grabCooldown <= 0 && dist < 3.5D && isWebVictim(target) && target.getBbHeight() < 4.0F) {
            this.startGrab(target);
            return;
        }
        if (--this.shotCooldown <= 0 && dist > 4.0D && dist < 18.0D && this.hasLineOfSight(target)) {
            this.shotCooldown = 50 + this.random.nextInt(30);
            this.shootWeb(target);
        }
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!this.level().isClientSide) {
            this.release(false);
            for (BlockPos pos : this.webs) {
                if (this.level().hasChunkAt(pos) && this.level().getBlockState(pos).is(Blocks.COBWEB)) {
                    this.level().removeBlock(pos, false);
                }
            }
            this.webs.clear();
        }
    }

    // ------------------------------------------------------------------ dados e barra de boss
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        long[] packed = new long[this.webs.size()];
        for (int i = 0; i < packed.length; i++) {
            packed[i] = this.webs.get(i).asLong();
        }
        tag.put("Webs", new LongArrayTag(packed));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.webs.clear();
        if (tag.contains("Webs", Tag.TAG_LONG_ARRAY)) {
            for (long l : tag.getLongArray("Webs")) {
                this.webs.add(BlockPos.of(l));
            }
        }
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossEvent.setName(this.getDisplayName());
    }
}
