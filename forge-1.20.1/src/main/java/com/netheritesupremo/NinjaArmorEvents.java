package com.netheritesupremo;

import com.netheritesupremo.item.NinjaArmorMaterial;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Conjunto ninja completo: Velocidade III, Pulo IV e furtividade (os mobs demoram para notar o jogador;
 * so o notam de perto ou se ele os atacou).
 */
public class NinjaArmorEvents {
    private static final String FX = "netheritesupremo_ninja_effects";
    private static final int SPEED_AMP = 2;
    private static final int JUMP_AMP = 3;
    /** Chance de um mob ignorar o jogador a cada tentativa de mira (elas se repetem varias vezes por segundo). */
    private static final float IGNORE_CHANCE = 0.93F;
    private static final double NOTICE_DISTANCE = 3.0D;

    public static boolean hasFullSet(LivingEntity entity) {
        for (EquipmentSlot slot : new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (!(stack.getItem() instanceof ArmorItem armor) || armor.getMaterial() != NinjaArmorMaterial.INSTANCE) {
                return false;
            }
        }
        return true;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase != TickEvent.Phase.END || player.level().isClientSide || player.tickCount % 10 != 0) {
            return;
        }
        CompoundTag data = player.getPersistentData();
        if (hasFullSet(player)) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, SPEED_AMP, true, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 40, JUMP_AMP, true, false, true));
            data.putBoolean(FX, true);
        } else if (data.getBoolean(FX)) {
            data.putBoolean(FX, false);
            MobEffectInstance speed = player.getEffect(MobEffects.MOVEMENT_SPEED);
            if (speed != null && speed.isAmbient() && speed.getAmplifier() == SPEED_AMP) {
                player.removeEffect(MobEffects.MOVEMENT_SPEED);
            }
            MobEffectInstance jump = player.getEffect(MobEffects.JUMP);
            if (jump != null && jump.isAmbient() && jump.getAmplifier() == JUMP_AMP) {
                player.removeEffect(MobEffects.JUMP);
            }
        }
    }

    /** Furtividade: o mob quase nunca escolhe o jogador de armadura ninja como alvo, a menos que esteja colado nele ou tenha sido atacado por ele. */
    @SubscribeEvent
    public void onChangeTarget(LivingChangeTargetEvent event) {
        if (!(event.getNewTarget() instanceof Player player) || !(event.getEntity() instanceof Mob mob)
                || !hasFullSet(player)) {
            return;
        }
        if (mob.getLastHurtByMob() == player || mob.distanceTo(player) < NOTICE_DISTANCE) {
            return;
        }
        if (mob.getRandom().nextFloat() < IGNORE_CHANCE) {
            event.setNewTarget(null);
        }
    }
}
