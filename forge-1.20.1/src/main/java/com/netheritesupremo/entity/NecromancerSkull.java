package com.netheritesupremo.entity;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

/**
 * Cabeca de wither do necromante. E uma WitherSkull (o cliente a desenha normalmente), mas causa o
 * dano de ataque de quem a lancou, nao acerta o proprio necromante nem os esqueletos invocados e
 * nao destroi blocos (ver {@code NecromancerEvents}).
 */
public class NecromancerSkull extends WitherSkull {
    public static final int WITHER_TICKS = 200;
    public static final int WITHER_AMPLIFIER = 1;

    public NecromancerSkull(Level level, LivingEntity shooter, double dx, double dy, double dz) {
        super(level, shooter, dx, dy, dz);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity)
                && !(entity instanceof Necromancer)
                && !entity.getTags().contains(Necromancer.MINION_TAG);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (this.level().isClientSide) {
            return;
        }
        Entity target = result.getEntity();
        Entity owner = this.getOwner();
        float damage = owner instanceof LivingEntity living
                ? (float) living.getAttributeValue(Attributes.ATTACK_DAMAGE)
                : 8.0F;
        if (target.hurt(this.damageSources().witherSkull(this, owner), damage) && target instanceof LivingEntity victim) {
            victim.addEffect(new MobEffectInstance(MobEffects.WITHER, WITHER_TICKS, WITHER_AMPLIFIER), owner);
        }
    }
}
