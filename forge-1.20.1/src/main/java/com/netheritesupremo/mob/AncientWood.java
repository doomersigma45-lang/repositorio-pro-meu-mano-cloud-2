package com.netheritesupremo.mob;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import com.netheritesupremo.registry.ModEntities;

/**
 * Ancient Wood (boss): versao de 7 blocos do Wood Stalker, 4700 HP, 70 de dano. Quebra qualquer bloco (menos
 * inquebraveis), escala tudo, nao tem medo, agarra por mais tempo e suga muito mais, regenera 5x mais perto de
 * plantas, invoca Wood Stalkers, fica mais forte com metade da vida e tem um Totem da Imortalidade (revive uma vez).
 */
public class AncientWood extends WoodStalker {
    public static final double MAX_HEALTH = 4700.0D;
    public static final double DAMAGE = 70.0D;
    private static final EntityDimensions CROUCH_BIG = EntityDimensions.scalable(1.8F, 3.5F);
    public static final int MAX_MINIONS = 4;

    private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(),
            BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.NOTCHED_10);
    private boolean enraged;
    private boolean totemUsed;
    private int summonCooldown = 200;

    public AncientWood(EntityType<? extends AncientWood> type, Level level) {
        super(type, level);
        this.xpReward = 600;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    // ---- habilidades "muito melhores"
    @Override
    protected float drainDamage() {
        return this.enraged ? 40.0F : 25.0F;
    }

    @Override
    protected int grabTicks() {
        return 160;
    }

    @Override
    protected int grabCooldownTicks() {
        return this.enraged ? 80 : 140;
    }

    @Override
    protected int grabChanceBound() {
        return 2;
    }

    @Override
    protected double holdDistance() {
        return 2.4D;
    }

    @Override
    protected double holdHeight() {
        return 2.2D;
    }

    @Override
    protected boolean fearsHazards() {
        return false;
    }

    @Override
    protected float maxBreakHardness() {
        return Float.MAX_VALUE;
    }

    @Override
    protected boolean breaksBlockEntities() {
        return true;
    }

    @Override
    protected int breakHalfWidth() {
        return 1;
    }

    @Override
    protected int breakHeight() {
        return 6;
    }

    @Override
    protected int breakInterval() {
        return 4;
    }

    @Override
    protected float regenMultiplier() {
        return 5.0F;
    }

    @Override
    protected EntityDimensions crouchDimensions() {
        return CROUCH_BIG;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return InteractionResult.PASS;     // chefe: nao se domestica
    }

    // ---- mais forte com metade da vida
    private void enrage() {
        this.enraged = true;
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(DAMAGE * 1.3D);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.36D);
        this.bossEvent.setColor(BossEvent.BossBarColor.RED);
        this.playSound(SoundEvents.RAVAGER_ROAR, 3.0F, 0.5F);
        if (this.level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.ANGRY_VILLAGER, this.getX(), this.getY() + 4.0D, this.getZ(), 40, 2.0D, 3.0D, 2.0D, 0.0D);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide || !(this.level() instanceof ServerLevel level)) {
            return;
        }
        if (!this.enraged && this.getHealth() <= this.getMaxHealth() / 2.0F) {
            this.enrage();
        }
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        if (--this.summonCooldown <= 0) {
            this.summonCooldown = this.enraged ? 240 : 400;
            this.summonStalkers(level, target);
        }
    }

    private void summonStalkers(ServerLevel level, LivingEntity target) {
        int cap = this.enraged ? MAX_MINIONS + 2 : MAX_MINIONS;
        int alive = level.getEntitiesOfClass(WoodStalker.class, this.getBoundingBox().inflate(40.0D), e -> e.getClass() == WoodStalker.class).size();
        for (int i = 0; i < 2 && alive < cap; i++, alive++) {
            WoodStalker w = ModEntities.WOOD_STALKER.get().create(level);
            if (w == null) {
                return;
            }
            w.moveTo(this.getX() + this.random.nextInt(9) - 4, this.getY(), this.getZ() + this.random.nextInt(9) - 4,
                    this.random.nextFloat() * 360.0F, 0.0F);
            w.setTarget(target);
            level.addFreshEntity(w);
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, w.getX(), w.getY() + 1.0D, w.getZ(), 12, 0.4D, 1.0D, 0.4D, 0.0D);
        }
    }

    // ---- totem da imortalidade
    @Override
    public void die(DamageSource source) {
        if (!this.totemUsed && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            this.totemUsed = true;
            this.setHealth(this.getMaxHealth() * 0.5F);
            this.removeAllEffects();
            this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
            this.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
            this.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
            this.level().broadcastEntityEvent(this, (byte) 35);
            if (!this.enraged) {
                this.enrage();
            }
            return;
        }
        super.die(source);
        if (this.level() instanceof ServerLevel level) {
            for (WoodStalker w : level.getEntitiesOfClass(WoodStalker.class, this.getBoundingBox().inflate(48.0D), e -> e.getClass() == WoodStalker.class && !e.isTame())) {
                w.discard();
            }
        }
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public boolean shouldDespawnInPeaceful() {
        return false;
    }

    /** Raro e unico: so um por regiao; nunca durante a geracao do mundo. */
    public static boolean checkBossSpawn(EntityType<AncientWood> type, ServerLevelAccessor level, MobSpawnType reason,
            BlockPos pos, RandomSource random) {
        if (reason == MobSpawnType.SPAWN_EGG || reason == MobSpawnType.COMMAND || reason == MobSpawnType.SPAWNER) {
            return true;
        }
        if (reason != MobSpawnType.NATURAL || random.nextInt(4) != 0) {
            return false;
        }
        ServerLevel sl = level.getLevel();
        return sl.getEntitiesOfClass(AncientWood.class, new AABB(pos).inflate(128.0D)).isEmpty()
                && net.minecraft.world.entity.monster.Monster.checkAnyLightMonsterSpawnRules(EntityType.ZOMBIE, level, reason, pos, random);
    }

    // ---- dados e barra de chefe
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Enraged", this.enraged);
        tag.putBoolean("TotemUsed", this.totemUsed);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.totemUsed = tag.getBoolean("TotemUsed");
        if (tag.getBoolean("Enraged")) {
            this.enrage();
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
