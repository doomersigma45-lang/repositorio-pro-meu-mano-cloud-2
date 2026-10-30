package com.netheritesupremo.client;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Pulo duplo com o kunai na mao: aperte espaco de novo no ar. */
@Mod.EventBusSubscriber(modid = NetheriteSupremo.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class DoubleJump {
    private static boolean jumpWasDown;
    private static boolean used;

    private DoubleJump() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        boolean jumpDown = Minecraft.getInstance().options.keyJump.isDown();
        if (player.onGround() || player.isInWater() || player.isInLava() || player.getAbilities().flying
                || player.isPassenger() || player.onClimbable()) {
            used = false;
        } else if (jumpDown && !jumpWasDown && !used && !player.isFallFlying()
                && (player.getMainHandItem().is(ModItems.KUNAI.get())
                        || player.getOffhandItem().is(ModItems.KUNAI.get()))) {
            used = true;
            Vec3 motion = player.getDeltaMovement();
            player.setDeltaMovement(motion.x, 0.62D, motion.z);
            player.fallDistance = 0.0F;
            player.level().playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP,
                    SoundSource.PLAYERS, 0.6F, 1.6F, false);
        }
        jumpWasDown = jumpDown;
    }
}
