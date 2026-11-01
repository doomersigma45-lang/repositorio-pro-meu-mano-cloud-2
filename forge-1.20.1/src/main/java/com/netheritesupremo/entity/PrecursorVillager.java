package com.netheritesupremo.entity;

import com.netheritesupremo.registry.ModEntities;
import com.netheritesupremo.trade.PrecursorTrades;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TradeWithPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.pathfinder.BlockPathTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Aldeao Precursor: variante pacifica e mais fraca do Precursor. Neutro, vive na agua e na terra, negocia
 * com ferro e diamante. Ao ver uma ameaca (monstro hostil) ou ser atacado, chama robos e todos os da especie.
 */
public class PrecursorVillager extends AbstractVillager {
    public static final int CALL_RADIUS = 48;
    private long nextRobotCall;

    public PrecursorVillager(EntityType<? extends PrecursorVillager> type, Level level) {
        super(type, level);
        this.moveControl = new SwimMoveControl(this);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.xpReward = 20;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 750.0D)
                .add(Attributes.ATTACK_DAMAGE, 25.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6D);
    }

    // ------------------------------------------------------------ agua e terra
    @Override
    protected PathNavigation createNavigation(Level level) {
        return new AmphibiousPathNavigation(this, level);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public void travel(Vec3 input) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(0.01F, input);
            this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(input);
        }
    }

    /** Nada em 3D na agua e anda normalmente na terra (igual ao afogado). */
    static class SwimMoveControl extends MoveControl {
        private final PrecursorVillager villager;

        SwimMoveControl(PrecursorVillager villager) {
            super(villager);
            this.villager = villager;
        }

        @Override
        public void tick() {
            LivingEntity target = this.villager.getTarget();
            if (this.villager.isInWater()) {
                if (target != null && target.getY() > this.villager.getY()) {
                    this.villager.setDeltaMovement(this.villager.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
                }
                if (this.operation != MoveControl.Operation.MOVE_TO || this.villager.getNavigation().isDone()) {
                    this.villager.setSpeed(0.0F);
                    return;
                }
                double dx = this.wantedX - this.villager.getX();
                double dy = this.wantedY - this.villager.getY();
                double dz = this.wantedZ - this.villager.getZ();
                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (dist < 1.0E-4D) {
                    return;
                }
                dx /= dist;
                dy /= dist;
                dz /= dist;
                float yaw = (float) (Mth.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
                this.villager.setYRot(this.rotlerp(this.villager.getYRot(), yaw, 90.0F));
                this.villager.yBodyRot = this.villager.getYRot();
                float speed = (float) (this.speedModifier * this.villager.getAttributeValue(Attributes.MOVEMENT_SPEED));
                float f = Mth.lerp(0.125F, this.villager.getSpeed(), speed);
                this.villager.setSpeed(f);
                this.villager.setDeltaMovement(this.villager.getDeltaMovement()
                        .add(f * dx * 0.005D, f * dy * 0.1D, f * dz * 0.005D));
            } else {
                if (!this.villager.onGround()) {
                    this.villager.setDeltaMovement(this.villager.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
                }
                super.tick();
            }
        }
    }

    // ------------------------------------------------------------ comportamento
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        this.goalSelector.addGoal(1, new LookAtTradingPlayerGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, PrecursorVillager.class).setAlertOthers());
        // Ameaca = monstro hostil a vista (jogadores so viram alvo se atacarem)
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false,
                e -> e instanceof Enemy));
    }

    /** Alvo novo (ameaca vista ou agressor): chama robos. */
    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);
        if (target != null && !this.level().isClientSide) {
            this.callRobots(target);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !this.level().isClientSide && source.getEntity() instanceof LivingEntity attacker
                && attacker != this && !(attacker instanceof PrecursorVillager) && !(attacker instanceof Robot)) {
            this.setTarget(attacker);
            // Chama todos da mesma especie ao redor para atacar o agressor
            List<PrecursorVillager> kin = this.level().getEntitiesOfClass(PrecursorVillager.class,
                    this.getBoundingBox().inflate(CALL_RADIUS), v -> v != this && v.isAlive());
            for (PrecursorVillager v : kin) {
                v.setTarget(attacker);
            }
        }
        return hurt;
    }

    private void callRobots(LivingEntity target) {
        if (!(this.level() instanceof ServerLevel level) || level.getGameTime() < this.nextRobotCall
                || target instanceof Robot || target instanceof PrecursorVillager) {
            return;
        }
        int nearby = level.getEntitiesOfClass(Robot.class, this.getBoundingBox().inflate(32.0D)).size();
        if (nearby >= 6) {
            return;
        }
        this.nextRobotCall = level.getGameTime() + 300;
        for (int i = 0; i < 2; i++) {
            Robot robot = ModEntities.ROBOT.get().create(level);
            if (robot == null) {
                continue;
            }
            double x = this.getX() + (this.random.nextDouble() - 0.5D) * 6.0D;
            double z = this.getZ() + (this.random.nextDouble() - 0.5D) * 6.0D;
            robot.moveTo(x, this.getY() + 1.5D, z, this.getYRot(), 0.0F);
            robot.finalizeSpawn(level, level.getCurrentDifficultyAt(robot.blockPosition()),
                    MobSpawnType.MOB_SUMMONED, null, null);
            robot.setTarget(target);
            level.addFreshEntity(robot);
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, this.getY() + 1.5D, z, 16, 0.4D, 0.6D, 0.4D, 0.05D);
        }
        level.playSound(null, this.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.NEUTRAL, 1.5F, 1.4F);
    }

    // ------------------------------------------------------------------ trocas
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof SpawnEggItem || !this.isAlive() || this.isTrading()
                || this.getTarget() != null || this.getOffers().isEmpty()) {
            return super.mobInteract(player, hand);
        }
        if (!this.level().isClientSide) {
            this.setTradingPlayer(player);
            this.openTradingScreen(player, this.getDisplayName(), 1);
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    protected void updateTrades() {
        PrecursorTrades.addTrades(this.getOffers());
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        if (offer.shouldRewardExp()) {
            this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5D, this.getZ(),
                    3 + this.random.nextInt(4)));
        }
    }

    /** Reabastece as trocas a cada ~5 minutos. */
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.tickCount % 6000 == 0 && !this.isTrading()) {
            for (MerchantOffer offer : this.getOffers()) {
                offer.resetUses();
            }
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) {
        return null;
    }

    @Override
    public boolean removeWhenFarAway(double distanceSqr) {
        return false;
    }
}
