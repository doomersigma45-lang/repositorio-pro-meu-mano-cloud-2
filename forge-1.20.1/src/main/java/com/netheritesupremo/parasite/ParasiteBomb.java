package com.netheritesupremo.parasite;

import com.netheritesupremo.coven.Coven;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

/**
 * Bomba parasita do Bombardeiro: ao bater, estoura numa nuvem que infecta os mobs e envenena os jogadores num raio de
 * 3,5 blocos. Nao destroi blocos.
 */
public class ParasiteBomb extends ThrowableItemProjectile {
    public ParasiteBomb(EntityType<? extends ParasiteBomb> type, Level level) {
        super(type, level);
    }

    public ParasiteBomb(Level level, LivingEntity owner) {
        super(Coven.PARASITE_BOMB.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.FERMENTED_SPIDER_EYE;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        level.sendParticles(ParticleTypes.SNEEZE, this.getX(), this.getY() + 0.3D, this.getZ(), 60, 1.6D, 0.8D, 1.6D, 0.02D);
        level.sendParticles(ParticleTypes.CRIMSON_SPORE, this.getX(), this.getY() + 0.3D, this.getZ(), 40, 1.6D, 0.8D, 1.6D, 0.02D);
        level.playSound(null, this.blockPosition(), SoundEvents.SLIME_SQUISH, SoundSource.HOSTILE, 1.5F, 0.6F);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(3.5D),
                e -> !(e instanceof ParasiteMob) && e.isAlive())) {
            e.hurt(e.damageSources().magic(), 5.0F);
            if (e instanceof Mob m) {
                ParasiteBehavior.infect(m);
            } else if (e instanceof Player) {
                e.addEffect(new MobEffectInstance(MobEffects.POISON, 120, 0));
                e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0));
            }
        }
        this.discard();
    }
}
