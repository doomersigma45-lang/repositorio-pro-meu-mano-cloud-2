package com.netheritesupremo.claudecontent.entity;

import com.netheritesupremo.claudecontent.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Boss guardião do Santuário das Raízes. */
public class MossColossusEntity extends Monster {
    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(getDisplayName(),
            BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.NOTCHED_10).setDarkenScreen(true);
    private int slamCooldown = 80;
    private boolean secondPhase;

    public MossColossusEntity(EntityType<? extends MossColossusEntity> type, Level level) {
        super(type, level);
        xpReward = 180;
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 240.0D)
                .add(Attributes.ATTACK_DAMAGE, 11.0D).add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.85D).add(Attributes.ARMOR, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 36.0D);
    }

    @Override protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.65D));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 24.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override protected void customServerAiStep() {
        super.customServerAiStep();
        bossEvent.setProgress(getHealth() / getMaxHealth());
        if (!secondPhase && getHealth() <= getMaxHealth() * 0.5F) {
            secondPhase = true;
            getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.29D);
            level().playSound(null, blockPosition(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 2.0F, 0.65F);
            if (level() instanceof net.minecraft.server.level.ServerLevel server)
                server.sendParticles(ParticleTypes.HAPPY_VILLAGER, getX(), getY() + 2, getZ(), 35, 1.2, 1.4, 1.2, 0.02);
        }
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) return;
        if (--slamCooldown <= 0 && distanceToSqr(target) < 8.0D * 8.0D && onGround()) {
            rootSlam();
            slamCooldown = secondPhase ? 75 : 125;
        }
    }

    private void rootSlam() {
        if (!(level() instanceof net.minecraft.server.level.ServerLevel server)) return;
        server.playSound(null, blockPosition(), SoundEvents.ROOTED_DIRT_BREAK, SoundSource.HOSTILE, 1.6F, 0.7F);
        server.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.MOSSY_COBBLESTONE.defaultBlockState()),
                getX(), getY() + 0.1, getZ(), 55, 3.0, 0.15, 3.0, 0.1D);
        double radius = secondPhase ? 6.0D : 4.5D;
        for (Player player : server.getEntitiesOfClass(Player.class, new AABB(blockPosition()).inflate(radius, 2, radius))) {
            if (player.isCreative() || player.isSpectator()) continue;
            Vec3 push = player.position().subtract(position()).normalize().scale(0.85D);
            player.setDeltaMovement(push.x, 0.48D, push.z);
            player.hurtMarked = true;
            player.hurt(damageSources().mobAttack(this), secondPhase ? 12.0F : 8.0F);
            if (secondPhase && player.isAlive()) player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 50, 0));
        }
    }

    @Override public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) living.addEffect(
                new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 35, 0));
        return hit;
    }

    @Override public void aiStep() {
        if (level().isClientSide) level().addParticle(ParticleTypes.SPORE_BLOSSOM_AIR,
                getRandomX(0.75D), getRandomY(), getRandomZ(0.75D), 0, 0.01, 0);
        super.aiStep();
    }

    @Override public void startSeenByPlayer(ServerPlayer player) { super.startSeenByPlayer(player); bossEvent.addPlayer(player); }
    @Override public void stopSeenByPlayer(ServerPlayer player) { super.stopSeenByPlayer(player); bossEvent.removePlayer(player); }
    @Override public void setCustomName(Component name) { super.setCustomName(name); bossEvent.setName(getDisplayName()); }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.IRON_GOLEM_STEP; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.IRON_GOLEM_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.IRON_GOLEM_DEATH; }

    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); tag.putBoolean("SecondPhase", secondPhase); }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        secondPhase = tag.getBoolean("SecondPhase");
        if (secondPhase) getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.29D);
        bossEvent.setProgress(getHealth() / getMaxHealth());
        if (hasCustomName()) bossEvent.setName(getDisplayName());
    }
}
