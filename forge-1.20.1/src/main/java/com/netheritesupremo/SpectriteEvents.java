package com.netheritesupremo;

import com.netheritesupremo.item.SpectriteArmorItem;
import com.netheritesupremo.registry.ModItems;
import java.util.List;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Vector3f;

/**
 * Efeitos do Spectrite: o set completo da armadura (com fome nao zerada) da Velocidade, Forca, Resistencia
 * e Resistencia ao Fogo; as espadas de Spectrite soltam uma explosao colorida em quem acertam.
 */
public class SpectriteEvents {
    private static boolean exploding;

    public static boolean hasFullSet(Player player) {
        for (EquipmentSlot slot : new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            if (!(player.getItemBySlot(slot).getItem() instanceof SpectriteArmorItem)) {
                return false;
            }
        }
        return true;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase != TickEvent.Phase.END || player.level().isClientSide || player.tickCount % 20 != 0) {
            return;
        }
        if (hasFullSet(player) && player.getFoodData().getFoodLevel() > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 0, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 1, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 1, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 0, true, false));
        }
    }

    @SubscribeEvent
    public void onHurt(LivingHurtEvent event) {
        if (exploding || !(event.getSource().getEntity() instanceof Player player)
                || event.getSource().getDirectEntity() != player
                || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        boolean special = stack.is(ModItems.SPECTRITE_SWORD_SPECIAL.get());
        if (!special && !stack.is(ModItems.SPECTRITE_SWORD.get())) {
            return;
        }
        LivingEntity target = event.getEntity();
        Vec3 at = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
        float radius = special ? 5.0F : 4.0F;
        float damage = special ? 12.0F : 8.0F;
        rainbowBurst(level, at, radius);
        level.playSound(null, at.x, at.y, at.z, SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.2F, 1.4F);
        exploding = true;
        try {
            List<LivingEntity> near = level.getEntitiesOfClass(LivingEntity.class,
                    target.getBoundingBox().inflate(radius),
                    e -> e != player && e != target && e.isAlive() && !e.isAlliedTo(player));
            for (LivingEntity e : near) {
                e.hurt(level.damageSources().explosion(player, player), damage);
            }
        } finally {
            exploding = false;
        }
    }

    /** Anel e esfera de particulas coloridas (ciclo do arco-iris) com fagulhas de fogos de artificio. */
    public static void rainbowBurst(ServerLevel level, Vec3 at, float radius) {
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, at.x, at.y, at.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        int n = 160;
        for (int i = 0; i < n; i++) {
            float hue = (float) i / n;
            int rgb = Mth.hsvToRgb(hue, 1.0F, 1.0F);
            Vector3f color = new Vector3f(((rgb >> 16) & 255) / 255.0F, ((rgb >> 8) & 255) / 255.0F, (rgb & 255) / 255.0F);
            double yaw = level.random.nextDouble() * Math.PI * 2.0D;
            double pitch = (level.random.nextDouble() - 0.5D) * Math.PI;
            double dx = Math.cos(yaw) * Math.cos(pitch), dy = Math.sin(pitch), dz = Math.sin(yaw) * Math.cos(pitch);
            level.sendParticles(new DustParticleOptions(color, 1.6F), at.x, at.y, at.z, 0, dx, dy, dz, radius * 0.18D);
            if (i % 8 == 0) {
                level.sendParticles(ParticleTypes.FIREWORK, at.x, at.y, at.z, 0, dx, dy, dz, radius * 0.12D);
            }
        }
    }
}
