package com.netheritesupremo.mob;

import com.netheritesupremo.registry.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

/** Abobora explosiva do Cavaleiro sem Cabeca: explode ao bater e envenena (Veneno II, 5 s) quem estiver perto. */
public class PumpkinBomb extends ThrowableItemProjectile {
    public PumpkinBomb(EntityType<? extends PumpkinBomb> type, Level level) {
        super(type, level);
    }

    public PumpkinBomb(Level level, LivingEntity owner) {
        super(ModEntities.PUMPKIN_BOMB.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.JACK_O_LANTERN;
    }

    private static boolean isFriend(LivingEntity e) {
        return e instanceof HeadlessHorseman || e instanceof HeadlessPup || e instanceof HorsemanSteed;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (this.level() instanceof ServerLevel level) {
            level.explode(this, this.getX(), this.getY(), this.getZ(), 2.0F, Level.ExplosionInteraction.MOB);
            level.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, this.getX(), this.getY() + 0.5D, this.getZ(), 40, 2.0D, 1.0D, 2.0D, 0.0D);
            for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0D), e -> !isFriend(e))) {
                e.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1));
            }
            this.discard();
        }
    }
}
