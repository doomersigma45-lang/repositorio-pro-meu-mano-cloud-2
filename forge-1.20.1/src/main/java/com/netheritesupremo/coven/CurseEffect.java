package com.netheritesupremo.coven;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * Maldicoes (inspiradas nas do Bewitchment): efeitos nocivos que causam um problema por segundo. Sao aplicados por
 * criaturas do covil e pela magia; leite remove como qualquer efeito.
 */
public class CurseEffect extends MobEffect {
    public enum Type { SOLAR_HATRED, HYDROPHOBIA, OVERHEATING, LIGHTNING_ROD, DARKNESS, MISFORTUNE, PARANOIA }

    private static final SoundEvent[] SCARES = {SoundEvents.CREEPER_PRIMED, SoundEvents.ZOMBIE_AMBIENT,
            SoundEvents.ENDERMAN_STARE, SoundEvents.WARDEN_HEARTBEAT, SoundEvents.SKELETON_AMBIENT,
            SoundEvents.GHAST_SCREAM, SoundEvents.WOODEN_DOOR_OPEN, SoundEvents.PHANTOM_AMBIENT};

    private final Type type;

    public CurseEffect(Type type, int color) {
        super(MobEffectCategory.HARMFUL, color);
        this.type = type;
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide || !(entity.level() instanceof ServerLevel level)) {
            return;
        }
        switch (this.type) {
            case SOLAR_HATRED -> {
                BlockPos pos = BlockPos.containing(entity.getX(), entity.getEyeY(), entity.getZ());
                if (level.isDay() && level.canSeeSky(pos) && !entity.isInWaterOrRain()) {
                    entity.setSecondsOnFire(3);
                }
            }
            case HYDROPHOBIA -> {
                if (entity.isInWaterOrRain()) {
                    entity.hurt(entity.damageSources().magic(), 2.0F + amplifier);
                }
            }
            case OVERHEATING -> {
                boolean hot = level.getBiome(entity.blockPosition()).value().getBaseTemperature() >= 1.0F
                        || level.dimensionType().ultraWarm() || entity.isOnFire();
                if (hot) {
                    entity.hurt(entity.damageSources().onFire(), 1.5F + amplifier);
                }
            }
            case LIGHTNING_ROD -> {
                if (level.isRaining() && level.canSeeSky(entity.blockPosition()) && level.random.nextInt(8) == 0) {
                    LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
                    if (bolt != null) {
                        bolt.moveTo(Vec3.atBottomCenterOf(entity.blockPosition()));
                        level.addFreshEntity(bolt);
                    }
                }
            }
            case DARKNESS -> entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0, false, false));
            case MISFORTUNE -> {
                entity.addEffect(new MobEffectInstance(MobEffects.UNLUCK, 60, 1, false, false));
                if (entity instanceof Player p && !p.getMainHandItem().isEmpty() && level.random.nextInt(15) == 0) {
                    p.drop(p.getInventory().removeItem(p.getInventory().selected, p.getMainHandItem().getCount()), true);
                }
            }
            case PARANOIA -> {
                if (entity instanceof ServerPlayer sp && level.random.nextInt(4) == 0) {
                    sp.playNotifySound(SCARES[level.random.nextInt(SCARES.length)], SoundSource.HOSTILE, 0.8F,
                            0.7F + level.random.nextFloat() * 0.5F);
                }
            }
        }
    }
}
