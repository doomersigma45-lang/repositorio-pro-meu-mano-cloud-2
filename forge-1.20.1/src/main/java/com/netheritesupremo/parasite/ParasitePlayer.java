package com.netheritesupremo.parasite;

import com.netheritesupremo.registry.ModItems;
import com.netheritesupremo.skeleton.OldGunItem;
import com.netheritesupremo.skeleton.SkeletonFaction;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * Jogador parasita: lider dos infectados. Persegue jogadores de longe, fica de tocaia antes de atacar, poe blocos,
 * abre portas, quebra vidro, puxa com vara de pesca, joga TNT na casa e usa equipamento que melhora a cada fase
 * (a forma nao muda, so a forca e o equipamento).
 */
public class ParasitePlayer extends Monster implements ParasiteMob {
    private static final String EQUIP_TAG = "ParasiteEquipPhase";

    private boolean aggressiveMode;
    private int stalkTicks;
    private int stalkLimit = 500;
    private int aggroTicks;
    private int rodCooldown = 100;
    private int tntCooldown = 400;
    private int placeCooldown;
    private int breakCooldown;
    private ItemStack stored = ItemStack.EMPTY;

    public ParasitePlayer(EntityType<? extends ParasitePlayer> type, Level level) {
        super(type, level);
        this.xpReward = 10;
        this.setCanPickUpLoot(true);
        if (this.getNavigation() instanceof GroundPathNavigation nav) {
            nav.setCanOpenDoors(true);
            nav.setCanFloat(true);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 96.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new GunGoal());
        this.goalSelector.addGoal(2, new StalkGoal());
        this.goalSelector.addGoal(3, new GatedMelee(this));
        this.goalSelector.addGoal(4, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(4, new BreakDoorGoal(this, difficulty -> true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, ParasiteMob.class));
        // enxerga jogadores de muito longe (alcance de 96 blocos, sem precisar de linha de visao)
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<Player>(this, Player.class, 0, false, false,
                e -> ParasiteBehavior.PREY.test(e)));
        this.targetSelector.addGoal(2, new ParasiteTargetGoal(this));
    }

    // ------------------------------------------------------------------ equipamento por fase
    private ItemStack enchanted(ItemStack stack, net.minecraft.world.item.enchantment.Enchantment e, int level) {
        stack.enchant(e, level);
        return stack;
    }

    private void equipFor(int phase) {
        ItemStack head = ItemStack.EMPTY;
        ItemStack chest;
        ItemStack legs;
        ItemStack feet;
        ItemStack sword;
        this.stored = ItemStack.EMPTY;
        if (phase <= 1) {
            chest = new ItemStack(Items.IRON_CHESTPLATE);
            legs = new ItemStack(Items.IRON_LEGGINGS);
            feet = new ItemStack(Items.IRON_BOOTS);
            sword = new ItemStack(Items.IRON_SWORD);
        } else if (phase == 2) {
            head = this.enchanted(this.enchanted(new ItemStack(Items.DIAMOND_HELMET), Enchantments.ALL_DAMAGE_PROTECTION, 3), Enchantments.UNBREAKING, 3);
            chest = this.enchanted(this.enchanted(new ItemStack(Items.DIAMOND_CHESTPLATE), Enchantments.ALL_DAMAGE_PROTECTION, 3), Enchantments.UNBREAKING, 3);
            legs = this.enchanted(this.enchanted(new ItemStack(Items.DIAMOND_LEGGINGS), Enchantments.ALL_DAMAGE_PROTECTION, 3), Enchantments.UNBREAKING, 3);
            feet = this.enchanted(this.enchanted(new ItemStack(Items.DIAMOND_BOOTS), Enchantments.ALL_DAMAGE_PROTECTION, 3), Enchantments.UNBREAKING, 3);
            sword = this.enchanted(this.enchanted(new ItemStack(Items.DIAMOND_SWORD), Enchantments.SHARPNESS, 5), Enchantments.UNBREAKING, 3);
        } else {
            head = new ItemStack(ModItems.OBSIDIAN_HELMET.get());
            chest = new ItemStack(ModItems.OBSIDIAN_CHESTPLATE.get());
            legs = new ItemStack(ModItems.OBSIDIAN_LEGGINGS.get());
            feet = new ItemStack(ModItems.OBSIDIAN_BOOTS.get());
            sword = new ItemStack(ModItems.OBSIDIAN_SWORD.get());
            this.stored = new ItemStack(ModItems.OLD_GUN.get());
        }
        this.setItemSlot(EquipmentSlot.HEAD, head);
        this.setItemSlot(EquipmentSlot.CHEST, chest);
        this.setItemSlot(EquipmentSlot.LEGS, legs);
        this.setItemSlot(EquipmentSlot.FEET, feet);
        this.setItemSlot(EquipmentSlot.MAINHAND, sword);
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.FISHING_ROD));
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            this.setDropChance(slot, slot == EquipmentSlot.OFFHAND ? 0.0F : 0.08F);
        }
    }

    private boolean holdsGun() {
        return this.getMainHandItem().is(ModItems.OLD_GUN.get());
    }

    private void swapWeapon() {
        ItemStack cur = this.getMainHandItem();
        this.setItemSlot(EquipmentSlot.MAINHAND, this.stored);
        this.stored = cur;
        this.setDropChance(EquipmentSlot.MAINHAND, 0.08F);
    }

    // ------------------------------------------------------------------ comportamento
    private boolean shouldAttack() {
        LivingEntity t = this.getTarget();
        if (t == null) {
            return false;
        }
        if (!(t instanceof Player)) {
            return true;
        }
        return this.aggressiveMode || this.distanceToSqr(t) < 36.0D;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide || !(this.level() instanceof ServerLevel level)) {
            return;
        }
        int phase = ParasiteBehavior.phaseOf(this);
        if (this.getPersistentData().getInt(EQUIP_TAG) != phase) {
            this.getPersistentData().putInt(EQUIP_TAG, phase);
            this.equipFor(phase);
        }
        if (phase >= 3 && this.stored.isEmpty()) {
            this.stored = this.holdsGun() ? new ItemStack(ModItems.OBSIDIAN_SWORD.get()) : new ItemStack(ModItems.OLD_GUN.get());
        }
        if (this.rodCooldown > 0) {
            this.rodCooldown--;
        }
        if (this.tntCooldown > 0) {
            this.tntCooldown--;
        }
        if (this.breakCooldown > 0) {
            this.breakCooldown--;
        }
        LivingEntity target = this.getTarget();
        this.updateMode(target);
        if (phase >= 3 && this.tickCount % 5 == 0) {
            double d = target == null ? 99.0D : this.distanceTo(target);
            if (this.holdsGun() && d < 6.0D) {
                this.swapWeapon();
            } else if (!this.holdsGun() && !this.stored.isEmpty() && this.aggressiveMode && d > 9.0D) {
                this.swapWeapon();
            }
        }
        if (target == null) {
            return;
        }
        if (this.aggressiveMode || !(target instanceof Player)) {
            this.buildTick(level, target);
        }
        if (this.breakCooldown <= 0 && (this.horizontalCollision || this.getNavigation().isStuck())) {
            this.breakCooldown = 10;
            this.breakGlassAndDoors(level, target);
        }
        if (this.aggressiveMode) {
            this.rodPull(level, target);
            this.throwTnt(level, target);
        }
        if (this.tickCount % 20 == 0) {
            this.rallyFollowers(level, target);
        }
    }

    /** Tocaia: segue o jogador sem atacar ate o momento certo; contra outros alvos ataca direto. */
    private void updateMode(LivingEntity target) {
        if (target instanceof Player p) {
            if (this.aggroTicks > 0) {
                this.aggroTicks--;
                this.aggressiveMode = true;
                if (this.aggroTicks == 0) {
                    this.aggressiveMode = false;
                    this.stalkTicks = 0;
                    this.stalkLimit = 400 + this.random.nextInt(600);
                }
            } else {
                this.stalkTicks++;
                boolean provoked = this.getLastHurtByMob() != null && this.tickCount - this.getLastHurtByMobTimestamp() < 100;
                if (this.stalkTicks > this.stalkLimit || provoked || p.getHealth() <= 8.0F || p.isSleeping()) {
                    this.aggroTicks = 1200;
                    this.aggressiveMode = true;
                }
            }
        } else {
            this.aggressiveMode = target != null;
        }
    }

    private void placeBlock(ServerLevel level, BlockPos pos) {
        level.setBlock(pos, Blocks.COBBLESTONE.defaultBlockState(), 3);
        this.swing(InteractionHand.MAIN_HAND);
        level.playSound(null, pos, SoundEvents.STONE_PLACE, SoundSource.HOSTILE, 1.0F, 0.8F);
    }

    /** Poe blocos: sobe em pilar se o alvo esta no alto e faz ponte sobre buracos. */
    private void buildTick(ServerLevel level, LivingEntity target) {
        if (this.placeCooldown > 0) {
            this.placeCooldown--;
            return;
        }
        if (!ForgeEventFactory.getMobGriefingEvent(level, this)) {
            return;
        }
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double hd = Math.sqrt(dx * dx + dz * dz);
        if (target.getY() - this.getY() > 2.5D && hd < 7.0D) {
            if (this.onGround()) {
                this.getJumpControl().jump();
            } else if (this.getDeltaMovement().y <= 0.2D) {
                BlockPos under = BlockPos.containing(this.getX(), this.getY() - 0.3D, this.getZ());
                if (level.getBlockState(under).canBeReplaced()) {
                    this.placeBlock(level, under);
                    this.placeCooldown = 6;
                }
            }
            return;
        }
        if (this.onGround() && hd > 2.0D && (this.getNavigation().isDone() || this.getNavigation().isStuck())) {
            Direction dir = Math.abs(dx) > Math.abs(dz) ? (dx > 0 ? Direction.EAST : Direction.WEST) : (dz > 0 ? Direction.SOUTH : Direction.NORTH);
            BlockPos front = this.blockPosition().relative(dir);
            if (level.getBlockState(front.below()).canBeReplaced() && level.getBlockState(front).isAir()) {
                this.placeBlock(level, front.below());
                this.placeCooldown = 8;
            }
        }
    }

    private static boolean breakable(BlockState s) {
        return s.is(BlockTags.DOORS) || s.is(BlockTags.TRAPDOORS) || s.is(BlockTags.FENCE_GATES)
                || s.is(Tags.Blocks.GLASS) || s.is(Tags.Blocks.GLASS_PANES);
    }

    private void breakGlassAndDoors(ServerLevel level, LivingEntity target) {
        if (!ForgeEventFactory.getMobGriefingEvent(level, this)) {
            return;
        }
        Vec3 d = target.position().subtract(this.position());
        Direction dir = Math.abs(d.x) > Math.abs(d.z) ? (d.x > 0 ? Direction.EAST : Direction.WEST) : (d.z > 0 ? Direction.SOUTH : Direction.NORTH);
        BlockPos base = this.blockPosition().relative(dir);
        for (int dy = 0; dy <= 2; dy++) {
            BlockPos p = base.above(dy);
            if (breakable(level.getBlockState(p))) {
                level.destroyBlock(p, true, this);
                this.swing(InteractionHand.MAIN_HAND);
            }
        }
    }

    /** Vara de pesca: puxa o alvo para perto. */
    private void rodPull(ServerLevel level, LivingEntity target) {
        double d = this.distanceTo(target);
        if (this.rodCooldown > 0 || d < 8.0D || d > 24.0D || !this.getSensing().hasLineOfSight(target)) {
            return;
        }
        this.rodCooldown = 200;
        this.swing(InteractionHand.OFF_HAND);
        Vec3 pull = this.position().subtract(target.position());
        Vec3 dir = new Vec3(pull.x, 0.0D, pull.z).normalize();
        target.setDeltaMovement(dir.x * 1.2D, 0.35D, dir.z * 1.2D);
        target.hurtMarked = true;
        for (int i = 1; i < 12; i++) {
            Vec3 p = this.getEyePosition().lerp(target.getEyePosition(), i / 12.0D);
            level.sendParticles(ParticleTypes.CRIT, p.x, p.y, p.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundSource.HOSTILE, 1.0F, 0.8F);
        level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.HOSTILE, 1.0F, 0.8F);
    }

    /** Joga TNT na casa do jogador (so com mobGriefing): quando nao ha caminho ate ele ou ele esta sob um teto. */
    private void throwTnt(ServerLevel level, LivingEntity target) {
        if (this.tntCooldown > 0 || !(target instanceof Player) || this.tickCount % 20 != 0) {
            return;
        }
        double d = this.distanceTo(target);
        if (d < 5.0D || d > 24.0D || !ForgeEventFactory.getMobGriefingEvent(level, this)) {
            return;
        }
        boolean noPath = this.getNavigation().createPath(target, 0) == null;
        boolean underRoof = !level.canSeeSky(target.blockPosition()) && this.random.nextInt(4) == 0;
        if (!noPath && !underRoof) {
            return;
        }
        this.tntCooldown = 1200;
        PrimedTnt tnt = new PrimedTnt(level, this.getX(), this.getEyeY(), this.getZ(), this);
        tnt.setFuse(50);
        Vec3 dir = target.position().subtract(this.position()).normalize();
        tnt.setDeltaMovement(dir.x * 0.7D, 0.45D, dir.z * 0.7D);
        level.addFreshEntity(tnt);
        this.swing(InteractionHand.MAIN_HAND);
        level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.TNT_PRIMED, SoundSource.HOSTILE, 1.0F, 1.0F);
    }

    /** Lider: os outros infectados por perto passam a atacar o mesmo alvo. */
    private void rallyFollowers(ServerLevel level, LivingEntity target) {
        if (!ParasiteBehavior.PREY.test(target)) {
            return;
        }
        List<Mob> mobs = level.getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(32.0D),
                m -> m != this && m instanceof ParasiteMob && !(m instanceof ParasitePlayer) && m.getTarget() == null);
        for (Mob m : mobs) {
            m.setTarget(target);
        }
    }

    // ------------------------------------------------------------------ objetivos
    private class GatedMelee extends MeleeAttackGoal {
        GatedMelee(PathfinderMob mob) {
            super(mob, 1.15D, true);
        }

        @Override
        public boolean canUse() {
            return ParasitePlayer.this.shouldAttack() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return ParasitePlayer.this.shouldAttack() && super.canContinueToUse();
        }
    }

    private class StalkGoal extends Goal {
        private int repath;

        StalkGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity t = ParasitePlayer.this.getTarget();
            return t instanceof Player && t.isAlive() && !ParasitePlayer.this.shouldAttack();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void tick() {
            LivingEntity t = ParasitePlayer.this.getTarget();
            if (t == null) {
                return;
            }
            ParasitePlayer self = ParasitePlayer.this;
            self.getLookControl().setLookAt(t, 30.0F, 30.0F);
            double d = self.distanceTo(t);
            if (d > 22.0D) {
                if (--this.repath <= 0) {
                    this.repath = 10;
                    self.getNavigation().moveTo(t, 1.0D);
                }
            } else if (d < 12.0D) {
                if (--this.repath <= 0) {
                    this.repath = 10;
                    Vec3 away = LandRandomPos.getPosAway(self, 10, 5, t.position());
                    if (away != null) {
                        self.getNavigation().moveTo(away.x, away.y, away.z, 1.1D);
                    }
                }
            } else {
                self.getNavigation().stop();
            }
        }
    }

    private class GunGoal extends Goal {
        private int cooldown = 20;

        GunGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity t = ParasitePlayer.this.getTarget();
            return t != null && t.isAlive() && ParasitePlayer.this.aggressiveMode && ParasitePlayer.this.holdsGun()
                    && ParasitePlayer.this.distanceToSqr(t) >= 64.0D;
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            ParasitePlayer.this.setAggressive(true);
        }

        @Override
        public void stop() {
            ParasitePlayer.this.setAggressive(false);
        }

        @Override
        public void tick() {
            LivingEntity t = ParasitePlayer.this.getTarget();
            if (t == null) {
                return;
            }
            ParasitePlayer self = ParasitePlayer.this;
            self.getLookControl().setLookAt(t, 30.0F, 30.0F);
            if (self.distanceToSqr(t) > 28.0D * 28.0D) {
                self.getNavigation().moveTo(t, 1.0D);
            } else {
                self.getNavigation().stop();
            }
            if (this.cooldown > 0) {
                this.cooldown--;
            }
            if (this.cooldown <= 0 && self.getSensing().hasLineOfSight(t) && self.distanceToSqr(t) <= 40.0D * 40.0D) {
                SkeletonFaction.fireGun(self, t);
                self.swing(InteractionHand.MAIN_HAND);
                this.cooldown = OldGunItem.RELOAD_TICKS + self.getRandom().nextInt(20);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("StalkTicks", this.stalkTicks);
        tag.putInt("AggroTicks", this.aggroTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.stalkTicks = tag.getInt("StalkTicks");
        this.aggroTicks = tag.getInt("AggroTicks");
    }
}
