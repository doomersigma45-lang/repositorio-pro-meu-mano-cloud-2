package com.netheritesupremo.mob;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.ForgeEventFactory;
import com.netheritesupremo.registry.ModItems;
import com.netheritesupremo.skeleton.OldGunItem;
import com.netheritesupremo.skeleton.SkeletonFaction;

/**
 * Hunter: mora numa cabana isolada na floresta. 250 HP, 7 de dano base, bem rapido. De longe atira com a Old Gun;
 * de perto troca para uma espada de diamante com Sharpness VII. Escala paredes, quebra vidro e portas e nao desiste
 * do alvo: so para quando o alvo morre ou quando outro mob o ataca (ai ele revida nesse mob).
 */
public class Hunter extends Monster {
    public static final double MAX_HEALTH = 250.0D;
    public static final double DAMAGE = 7.0D;
    public static final double SPEED = 0.36D;      // ~2,8x a corrida de um jogador (0,13)
    private static final EntityDataAccessor<Byte> DATA_CLIMB = SynchedEntityData.defineId(Hunter.class, EntityDataSerializers.BYTE);

    private ItemStack stored = ItemStack.EMPTY;    // a arma que nao esta na mao
    private boolean homeSet;
    private int breakCooldown;
    @Nullable
    private LivingEntity hunted;

    public Hunter(EntityType<? extends Hunter> type, Level level) {
        super(type, level);
        this.xpReward = 40;
        this.setPathfindingMalus(net.minecraft.world.level.pathfinder.BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(net.minecraft.world.level.pathfinder.BlockPathTypes.WATER_BORDER, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, SPEED)
                .add(Attributes.FOLLOW_RANGE, 64.0D)
                .add(net.minecraftforge.common.ForgeMod.SWIM_SPEED.get(), 2.5D);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        WallClimberNavigation nav = new WallClimberNavigation(this, level);
        nav.setCanFloat(true);
        return nav;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_CLIMB, (byte) 0);
    }

    @Override
    public boolean onClimbable() {
        return (this.entityData.get(DATA_CLIMB) & 1) != 0;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new HunterGunGoal());
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 0.7D));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.5D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));          // outro mob o ataca: ele revida nesse mob
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 0, false, false, null));
    }

    // ------------------------------------------------------------------ armas
    private ItemStack makeGun() {
        ItemStack gun = new ItemStack(ModItems.OLD_GUN.get());
        gun.enchant(Enchantments.UNBREAKING, 3);
        gun.enchant(Enchantments.MENDING, 1);
        gun.enchant(Enchantments.SHARPNESS, 5);
        return gun;
    }

    private ItemStack makeSword() {
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        sword.enchant(Enchantments.SHARPNESS, 7);
        return sword;
    }

    private void equipInitial() {
        this.setItemSlot(EquipmentSlot.MAINHAND, this.makeGun());
        this.stored = this.makeSword();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            this.setDropChance(slot, 0.0F);       // o drop vem do loot table
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor level, DifficultyInstance difficulty,
            MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        data = super.finalizeSpawn(level, difficulty, reason, data, tag);
        this.equipInitial();
        return data;
    }

    private void swapWeapon() {
        ItemStack cur = this.getMainHandItem();
        this.setItemSlot(EquipmentSlot.MAINHAND, this.stored);
        this.stored = cur;
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    private boolean holdsGun() {
        return this.getMainHandItem().is(ModItems.OLD_GUN.get());
    }

    // ------------------------------------------------------------------ alvo persistente
    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);
        if (target != null) {
            this.hunted = target;
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide || !(this.level() instanceof ServerLevel level)) {
            return;
        }
        if (this.getMainHandItem().isEmpty() && this.stored.isEmpty()) {
            this.equipInitial();                  // nasceu de uma estrutura (sem finalizeSpawn)
        }
        if (!this.homeSet && this.tickCount > 5) {
            this.homeSet = true;
            this.restrictTo(this.blockPosition(), 24);
        }
        if (this.breakCooldown > 0) {
            this.breakCooldown--;
        }
        // nunca desiste: so solta o alvo quando ele morre
        if (this.hunted != null && (!this.hunted.isAlive() || this.hunted.level() != this.level())) {
            this.hunted = null;
        }
        if (this.getTarget() == null && this.hunted != null) {
            super.setTarget(this.hunted);
        }
        LivingEntity target = this.getTarget();
        this.entityData.set(DATA_CLIMB, (byte) (this.horizontalCollision ? 1 : 0));
        if (this.tickCount % 5 == 0) {
            double d = target == null ? 99.0D : this.distanceTo(target);
            if (d < 5.0D && this.holdsGun()) {
                this.swapWeapon();
            } else if ((target == null || d > 8.0D) && !this.holdsGun() && !this.getMainHandItem().isEmpty()) {
                this.swapWeapon();
            }
        }
        if (target != null && this.breakCooldown <= 0 && (this.horizontalCollision || this.getNavigation().isStuck())) {
            this.breakCooldown = 10;
            this.breakGlassAndDoors(level, target);
        }
    }

    private static boolean breakable(BlockState s) {
        return s.is(BlockTags.DOORS) || s.is(BlockTags.TRAPDOORS) || s.is(Tags.Blocks.GLASS) || s.is(Tags.Blocks.GLASS_PANES);
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

    // ------------------------------------------------------------------ dados
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("StoredWeapon", this.stored.save(new CompoundTag()));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("StoredWeapon")) {
            this.stored = ItemStack.of(tag.getCompound("StoredWeapon"));
        }
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    /** Atira com a Old Gun de longe, sem parar de avancar (so recarrega entre os tiros). */
    private class HunterGunGoal extends Goal {
        private int cooldown = 20;

        HunterGunGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity t = Hunter.this.getTarget();
            return t != null && t.isAlive() && Hunter.this.holdsGun() && Hunter.this.distanceToSqr(t) >= 25.0D;
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
            Hunter.this.setAggressive(true);
        }

        @Override
        public void stop() {
            Hunter.this.setAggressive(false);
        }

        @Override
        public void tick() {
            LivingEntity t = Hunter.this.getTarget();
            if (t == null) {
                return;
            }
            Hunter.this.getLookControl().setLookAt(t, 30.0F, 30.0F);
            Hunter.this.getNavigation().moveTo(t, 1.0D);
            if (this.cooldown > 0) {
                this.cooldown--;
            }
            if (this.cooldown <= 0 && Hunter.this.getSensing().hasLineOfSight(t) && Hunter.this.distanceToSqr(t) <= 40.0D * 40.0D) {
                SkeletonFaction.fireGun(Hunter.this, t);
                Hunter.this.swing(InteractionHand.MAIN_HAND);
                this.cooldown = OldGunItem.RELOAD_TICKS + Hunter.this.getRandom().nextInt(20);
            }
        }
    }
}
