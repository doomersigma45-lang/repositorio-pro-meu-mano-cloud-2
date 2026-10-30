package com.netheritesupremo.entity;

import com.netheritesupremo.registry.ModEntities;
import com.netheritesupremo.registry.ModItems;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * Samurai: boss de Cherry Grove (so um por vez). 250 de vida, 15 de dano que cresce conforme ele perde vida
 * (ate 3x), armadura de diamante invisivel, katana, arremessa salvas de shurikens e chama ninjas.
 */
public class Samurai extends Monster {
    public static final float BASE_DAMAGE = 15.0F;
    public static final float SHURIKEN_DAMAGE = 10.0F;

    private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(),
            BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);

    public Samurai(EntityType<? extends Samurai> type, Level level) {
        super(type, level);
        this.xpReward = 250;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createSamuraiAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 250.0D)
                .add(Attributes.ATTACK_DAMAGE, BASE_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D);
    }

    /** Spawn natural: nada de luz minima, mas so se nao houver outro Samurai carregado nesta dimensao. */
    public static boolean checkSpawnRules(EntityType<? extends Samurai> type, ServerLevelAccessor level,
            MobSpawnType reason, BlockPos pos, RandomSource random) {
        if (!Mob.checkMobSpawnRules(type, level, reason, pos, random)) {
            return false;
        }
        for (Entity e : level.getLevel().getAllEntities()) {
            if (e instanceof Samurai && e.isAlive()) {
                return false;
            }
        }
        return random.nextInt(3) == 0;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.KATANA.get()));
        // Armadura completa de diamante (invisivel: o renderer nao desenha armadura)
        this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
        this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
        this.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            this.setDropChance(slot, 0.0F);
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
            MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        data = super.finalizeSpawn(level, difficulty, reason, data, tag);
        this.populateDefaultEquipmentSlots(level.getRandom(), difficulty);
        return data;
    }

    /** Quanto menos vida, mais dano: de 1x (vida cheia) a 3x (quase morto). */
    public float currentDamage() {
        float missing = 1.0F - this.getHealth() / this.getMaxHealth();
        return BASE_DAMAGE * (1.0F + 2.0F * Math.max(0.0F, Math.min(1.0F, missing)));
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        this.swing(InteractionHand.MAIN_HAND);
        boolean hit = target.hurt(this.damageSources().mobAttack(this), this.currentDamage());
        if (hit && target instanceof LivingEntity living) {
            living.knockback(0.6D, this.getX() - living.getX(), this.getZ() - living.getZ());
        }
        return hit;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SummonNinjasGoal(this));
        this.goalSelector.addGoal(2, new ShurikenVolleyGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.15D, false));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    /** Chama ninjas (2, ou 3 com menos de metade da vida) enquanto houver menos de 6 por perto. */
    static class SummonNinjasGoal extends Goal {
        private final Samurai boss;
        private int cooldown = 80;

        SummonNinjasGoal(Samurai boss) {
            this.boss = boss;
        }

        @Override
        public boolean canUse() {
            if (this.cooldown > 0) {
                this.cooldown--;
                return false;
            }
            LivingEntity target = this.boss.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            this.cooldown = 140;
            if (!(this.boss.level() instanceof ServerLevel level) || this.boss.getTarget() == null) {
                return;
            }
            List<Ninja> around = level.getEntitiesOfClass(Ninja.class, this.boss.getBoundingBox().inflate(32.0D));
            if (around.size() >= 6) {
                return;
            }
            int count = this.boss.getHealth() < this.boss.getMaxHealth() / 2.0F ? 3 : 2;
            for (int i = 0; i < count; i++) {
                Ninja ninja = ModEntities.NINJA.get().create(level);
                if (ninja == null) {
                    continue;
                }
                double x = this.boss.getX() + (this.boss.getRandom().nextDouble() - 0.5D) * 8.0D;
                double z = this.boss.getZ() + (this.boss.getRandom().nextDouble() - 0.5D) * 8.0D;
                ninja.moveTo(x, this.boss.getY(), z, this.boss.getYRot(), 0.0F);
                ninja.finalizeSpawn(level, level.getCurrentDifficultyAt(ninja.blockPosition()),
                        MobSpawnType.MOB_SUMMONED, null, null);
                ninja.setTarget(this.boss.getTarget());
                level.addFreshEntity(ninja);
                level.sendParticles(ParticleTypes.POOF, x, this.boss.getY() + 1.0D, z, 12, 0.3D, 0.5D, 0.3D, 0.02D);
            }
            level.playSound(null, this.boss.blockPosition(), SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.HOSTILE,
                    1.5F, 1.2F);
        }
    }

    /** Salva de 5 shurikens em leque, de 5 a 20 blocos de distancia. */
    static class ShurikenVolleyGoal extends Goal {
        private final Samurai boss;
        private int cooldown = 60;

        ShurikenVolleyGoal(Samurai boss) {
            this.boss = boss;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.cooldown > 0) {
                this.cooldown--;
                return false;
            }
            LivingEntity target = this.boss.getTarget();
            if (target == null || !target.isAlive()) {
                return false;
            }
            double dist = this.boss.distanceTo(target);
            return dist >= 5.0D && dist <= 20.0D && this.boss.hasLineOfSight(target);
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            this.cooldown = 50;
            LivingEntity target = this.boss.getTarget();
            if (target == null) {
                return;
            }
            this.boss.swing(InteractionHand.MAIN_HAND);
            for (int i = 0; i < 5; i++) {
                ShurikenEntity shuriken = new ShurikenEntity(this.boss.level(), this.boss);
                shuriken.setFixedDamage(SHURIKEN_DAMAGE);
                shuriken.setItem(new ItemStack(ModItems.SHURIKEN.get()));
                double dx = target.getX() - this.boss.getX();
                double dy = target.getEyeY() - 0.2D - shuriken.getY();
                double dz = target.getZ() - this.boss.getZ();
                double horizontal = Math.sqrt(dx * dx + dz * dz);
                shuriken.shoot(dx, dy + horizontal * 0.05D, dz, 1.9F, 8.0F);
                this.boss.level().addFreshEntity(shuriken);
            }
            this.boss.level().playSound(null, this.boss.blockPosition(), SoundEvents.SNOWBALL_THROW,
                    SoundSource.HOSTILE, 1.0F, 1.5F);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PLAYER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PLAYER_DEATH;
    }

    // --------------------------------------------------------------- barra de boss
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
