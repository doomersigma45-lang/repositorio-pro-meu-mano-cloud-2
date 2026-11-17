package com.netheritesupremo.parasite;

import com.netheritesupremo.registry.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Estourador Parasita: bolsa de carne que se aproxima e infla como um creeper, mas o "estouro" nao e explosao de verdade
 * (raio 0, sem dano a blocos): solta 4 vermes, 3 Aranhas Parasitas e 1 parasita, infecta mobs num raio de 6 blocos e
 * deixa jogadores por perto envenenados e enjoados. Tambem estoura ao morrer.
 */
public class ParasiteBurster extends Creeper implements ParasiteMob {
    private boolean burst;

    public ParasiteBurster(EntityType<? extends Creeper> type, Level level) {
        super(type, level);
        this.xpReward = 25;
        CompoundTag tag = new CompoundTag(); // explosao de raio 0 (o estouro e feito por afterBurst)
        tag.putByte("ExplosionRadius", (byte) 0);
        tag.putShort("Fuse", (short) 25);
        super.readAdditionalSaveData(tag);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Creeper.createAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.27D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SwellGoal(this));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        ParasiteBehavior.addTargets(this);
    }

    /** O Creeper chama discard() ao "explodir": aproveitamos a remocao para o estouro. */
    @Override
    public void remove(RemovalReason reason) {
        if (!this.burst && reason == RemovalReason.DISCARDED && this.level() instanceof ServerLevel level
                && this.getSwelling(1.0F) > 0.85F) {
            this.afterBurst(level);
        }
        super.remove(reason);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!this.burst && this.level() instanceof ServerLevel level) {
            this.afterBurst(level);
        }
    }

    private void afterBurst(ServerLevel level) {
        this.burst = true;
        int stage = ParasiteBehavior.phaseOf(this);
        for (Mob m : level.getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(6.0D), e -> !(e instanceof ParasiteMob))) {
            ParasiteBehavior.infect(m);
        }
        for (Player p : level.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(5.0D))) {
            p.addEffect(new MobEffectInstance(MobEffects.POISON, 80, 0));
            p.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0));
        }
        for (int i = 0; i < 8; i++) {
            EntityType<? extends Mob> type = i < 4 ? ModEntities.PARASITE_WORM.get()
                    : (i < 7 ? ModEntities.PARASITE_SPIDER.get()
                    : (level.random.nextBoolean() ? ModEntities.PARASITE_ZOMBIE.get() : ModEntities.PARASITE_SKELETON.get()));
            Mob m = type.create(level);
            if (m == null) {
                continue;
            }
            m.moveTo(this.getX() + level.random.nextDouble() * 3.0D - 1.5D, this.getY() + 0.3D,
                    this.getZ() + level.random.nextDouble() * 3.0D - 1.5D, level.random.nextFloat() * 360.0F, 0.0F);
            OutbreakEvents.stampStage(m, stage);
            level.addFreshEntity(m);
        }
        level.sendParticles(ParticleTypes.SNEEZE, this.getX(), this.getY() + 0.7D, this.getZ(), 80, 1.0D, 0.6D, 1.0D, 0.08D);
        level.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + 0.7D, this.getZ(), 20, 0.8D, 0.5D, 0.8D, 0.05D);
        level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.SLIME_SQUISH, SoundSource.HOSTILE, 2.0F, 0.4F);
    }
}
