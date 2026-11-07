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
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
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
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Rei Esqueleto (boss): esqueleto gigante de coroa, 7500 de vida e 100 de dano. Invoca as tres
 * variantes de esqueleto (comum, stray e wither) com armadura e espada de Spectrite e arremessa bolas
 * de fogo e cabecas de wither. Com metade da vida invoca dois Necromantes e fica 3x mais forte e 3x
 * mais resistente.
 */
public class SkeletonKing extends Monster implements RangedAttackMob {
    public static final double MAX_HEALTH = 7500.0D;
    public static final double ATTACK_DAMAGE = 100.0D;
    public static final float PHASE2_MULTIPLIER = 3.0F;

    public static final String MINION_TAG = "netheritesupremo_king_minion";
    public static final String NECRO_TAG = "netheritesupremo_king_necro";
    public static final int MAX_MINIONS = 9;
    public static final int MINION_LIFETIME_TICKS = 20 * 120;

    private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(),
            BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.NOTCHED_10);
    private boolean phase2;

    public SkeletonKing(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 600;
        this.setPersistenceRequired();
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.SPECTRITE_SWORD.get()));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.27D)
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ARMOR, 20.0D);
    }

    /** O Rei, seus esqueletos e os Necromantes invocados por ele sao do mesmo lado. */
    public static boolean isKingSide(Entity e) {
        return e instanceof SkeletonKing || e.getTags().contains(MINION_TAG) || e.getTags().contains(NECRO_TAG);
    }

    public boolean isPhase2() {
        return phase2;
    }

    // ------------------------------------------------------------------ IA
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SummonMinionsGoal(this));
        this.goalSelector.addGoal(2, new ProjectileGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, false, false, null));
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target != null && isKingSide(target)) {
            return;
        }
        super.setTarget(target);
    }

    /** Bola de fogo e cabeca de wither se alternam. */
    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        double sx = this.getX();
        double sy = this.getY() + 2.6D;
        double sz = this.getZ();
        double dx = target.getX() - sx;
        double dy = target.getY(0.5D) - sy;
        double dz = target.getZ() - sz;
        Vec3 forward = new Vec3(dx, dy, dz).normalize();
        if (this.random.nextBoolean()) {
            KingFireball ball = new KingFireball(this.level(), this, dx, dy, dz, phase2 ? 3 : 2);
            ball.setPos(sx + forward.x * 1.5D, sy + forward.y * 1.5D, sz + forward.z * 1.5D);
            this.level().addFreshEntity(ball);
            this.playSound(SoundEvents.GHAST_SHOOT, 1.5F, 0.7F);
        } else {
            NecromancerSkull skull = new NecromancerSkull(this.level(), this, dx, dy, dz);
            skull.damageScale = 0.5F;
            skull.setPos(sx + forward.x * 1.5D, sy + forward.y * 1.5D, sz + forward.z * 1.5D);
            this.level().addFreshEntity(skull);
            this.playSound(SoundEvents.WITHER_SHOOT, 1.5F, 0.8F);
        }
    }

    // ------------------------------------------------------ esqueletos invocados
    private List<Mob> minions() {
        return this.level().getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(64.0D),
                m -> m.isAlive() && m.getTags().contains(MINION_TAG));
    }

    private void summonMinions(LivingEntity target) {
        if (!(this.level() instanceof ServerLevel server)) {
            return;
        }
        if (this.minions().size() > MAX_MINIONS - 3) {
            return;
        }
        EntityType<?>[] kinds = {EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON};
        for (int i = 0; i < kinds.length; i++) {
            Mob mob = (Mob) kinds[i].create(server);
            if (mob == null) {
                continue;
            }
            double angle = Math.PI * 2.0D * i / kinds.length + this.random.nextDouble() * 0.6D;
            double x = this.getX() + Math.cos(angle) * 4.0D;
            double z = this.getZ() + Math.sin(angle) * 4.0D;
            mob.moveTo(x, this.getY(), z, this.random.nextFloat() * 360.0F, 0.0F);
            mob.finalizeSpawn(server, server.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
            mob.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.SPECTRITE_HELMET.get()));
            mob.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.SPECTRITE_CHESTPLATE.get()));
            mob.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.SPECTRITE_LEGGINGS.get()));
            mob.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.SPECTRITE_BOOTS.get()));
            mob.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.SPECTRITE_SWORD.get()));
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                mob.setDropChance(slot, 0.0F);
            }
            mob.addTag(MINION_TAG);
            mob.setPersistenceRequired();
            mob.setTarget(target);
            server.addFreshEntity(mob);
            server.sendParticles(ParticleTypes.SOUL, mob.getX(), mob.getY() + 1.0D, mob.getZ(), 12, 0.3D, 0.6D, 0.3D, 0.02D);
        }
        this.playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 1.5F, 0.6F);
    }

    // ----------------------------------------------------------------- fase 2
    private void enterPhase2() {
        this.phase2 = true;
        this.applyPhase2Stats();
        this.setHealth(Math.max(this.getHealth(), 1.0F));
        this.bossEvent.setColor(BossEvent.BossBarColor.RED);
        this.playSound(SoundEvents.WITHER_SPAWN, 2.0F, 0.6F);
        if (this.level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY() + 1.5D, this.getZ(),
                    80, 1.0D, 1.6D, 1.0D, 0.06D);
            for (int i = 0; i < 2; i++) {
                Necromancer necro = ModEntities.NECROMANCER.get().create(server);
                if (necro == null) {
                    continue;
                }
                double angle = Math.PI * i + this.random.nextDouble();
                necro.moveTo(this.getX() + Math.cos(angle) * 4.0D, this.getY() + 2.0D, this.getZ() + Math.sin(angle) * 4.0D,
                        this.random.nextFloat() * 360.0F, 0.0F);
                necro.finalizeSpawn(server, server.getCurrentDifficultyAt(necro.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                necro.addTag(NECRO_TAG);
                if (this.getTarget() != null) {
                    necro.setTarget(this.getTarget());
                }
                server.addFreshEntity(necro);
            }
        }
    }

    private void applyPhase2Stats() {
        var dmg = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmg != null) {
            dmg.setBaseValue(ATTACK_DAMAGE * PHASE2_MULTIPLIER);
        }
    }

    /** Fase 2: recebe 1/3 do dano (3x mais resistente). */
    @Override
    protected void actuallyHurt(DamageSource source, float amount) {
        super.actuallyHurt(source, this.phase2 ? amount / PHASE2_MULTIPLIER : amount);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        return effect.getEffect() != MobEffects.WITHER && super.canBeAffected(effect);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        if (!this.phase2 && this.getHealth() <= this.getMaxHealth() / 2.0F) {
            this.enterPhase2();
        }
        if (this.tickCount % 20 == 0) {
            for (Mob m : this.minions()) {
                if (m.tickCount > MINION_LIFETIME_TICKS) {
                    m.kill();
                }
            }
        }
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!this.level().isClientSide) {
            for (Mob m : this.minions()) {
                m.kill();
            }
            for (Necromancer n : this.level().getEntitiesOfClass(Necromancer.class, this.getBoundingBox().inflate(64.0D),
                    n -> n.getTags().contains(NECRO_TAG))) {
                n.kill();
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Phase2", this.phase2);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.phase2 = tag.getBoolean("Phase2");
        if (this.phase2) {
            this.applyPhase2Stats();
            this.bossEvent.setColor(BossEvent.BossBarColor.RED);
        }
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
    }

    // -------------------------------------------------------------- barra de boss
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

    // ---------------------------------------------------------------- sons
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }

    @Override
    public float getVoicePitch() {
        return 0.55F;
    }

    // ============================================================ objetivos
    /** A cada ~3 s lanca bola de fogo ou cabeca de wither no alvo (2 s na fase 2). */
    static class ProjectileGoal extends Goal {
        private final SkeletonKing king;
        private int next = 60;

        ProjectileGoal(SkeletonKing king) {
            this.king = king;
            this.setFlags(EnumSet.noneOf(Flag.class));
        }

        @Override
        public boolean canUse() {
            LivingEntity t = king.getTarget();
            if (t == null || !t.isAlive() || king.tickCount < next) {
                return false;
            }
            double d = king.distanceToSqr(t);
            return d >= 36.0D && d <= 40.0D * 40.0D && king.hasLineOfSight(t);
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            LivingEntity t = king.getTarget();
            if (t != null) {
                king.getLookControl().setLookAt(t, 30.0F, 30.0F);
                king.performRangedAttack(t, 1.0F);
            }
            next = king.tickCount + (king.phase2 ? 30 : 50) + king.getRandom().nextInt(30);
        }
    }

    /** Invoca os tres esqueletos de Spectrite a cada ~9 s durante a luta. */
    static class SummonMinionsGoal extends Goal {
        private final SkeletonKing king;
        private int next = 40;

        SummonMinionsGoal(SkeletonKing king) {
            this.king = king;
            this.setFlags(EnumSet.noneOf(Flag.class));
        }

        @Override
        public boolean canUse() {
            LivingEntity t = king.getTarget();
            return t != null && t.isAlive() && king.tickCount >= next;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            LivingEntity t = king.getTarget();
            if (t != null) {
                king.summonMinions(t);
            }
            next = king.tickCount + (king.phase2 ? 120 : 180) + king.getRandom().nextInt(60);
        }
    }
}
