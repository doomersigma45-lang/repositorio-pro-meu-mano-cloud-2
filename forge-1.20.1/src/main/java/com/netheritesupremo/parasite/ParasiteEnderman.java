package com.netheritesupremo.parasite;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.Level;

/**
 * Enderman parasita: muito mais forte que o comum, nao pega blocos, caca tudo que nao e parasita e infecta o que morde.
 * Se o alvo esta longe (mais de 16 blocos) ele se teletransporta para perto dele, levando junto ate 4 parasitas por perto.
 */
public class ParasiteEnderman extends EnderMan implements ParasiteMob {
    private int teleportCooldown = 40;

    public ParasiteEnderman(EntityType<? extends ParasiteEnderman> type, Level level) {
        super(type, level);
        this.xpReward = 20;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EnderMan.createAttributes()
                .add(Attributes.MAX_HEALTH, 150.0D)
                .add(Attributes.ATTACK_DAMAGE, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.36D)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 64.0D);
    }

    @Override
    protected void registerGoals() {
        ParasiteBehavior.addMeleeGoals(this, 1.2D);
        ParasiteBehavior.addTargets(this);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            return;
        }
        if (this.teleportCooldown > 0) {
            this.teleportCooldown--;
        }
        LivingEntity target = this.getTarget();
        if (target != null && this.teleportCooldown <= 0 && this.tickCount % 10 == 0 && this.distanceToSqr(target) > 256.0D) {
            for (int i = 0; i < 10; i++) {
                double angle = this.random.nextDouble() * Math.PI * 2.0D;
                double radius = 2.5D + this.random.nextDouble() * 2.0D;
                double x = target.getX() + Math.cos(angle) * radius;
                double z = target.getZ() + Math.sin(angle) * radius;
                java.util.List<net.minecraft.world.entity.Mob> mates = ParasiteGeneralEvents.mates(this);
                if (this.randomTeleport(x, target.getY(), z, true)) {
                    ParasiteGeneralEvents.moveMates(mates, this.getX(), this.getY(), this.getZ());
                    this.teleportCooldown = 60;
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENDERMAN_TELEPORT,
                            SoundSource.HOSTILE, 1.0F, 0.7F);
                    break;
                }
            }
        }
    }
}
