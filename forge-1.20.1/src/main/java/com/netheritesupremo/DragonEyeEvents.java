package com.netheritesupremo;

import com.netheritesupremo.registry.ModItems;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Olho do Dragao. Segurando (qualquer mao): voo rapido (mais que cavalo), +15 coracoes (+30 de vida maxima)
 * e dobro de forca (dano de ataque x2). O Ender Dragon dropa o item ao morrer.
 */
public class DragonEyeEvents {
    private static final UUID HEALTH_ID = UUID.fromString("5d0f6a1e-7c11-4a0e-9a53-0d2b6c1e7a01");
    private static final UUID DAMAGE_ID = UUID.fromString("5d0f6a1e-7c11-4a0e-9a53-0d2b6c1e7a02");
    private static final String FLY = "netheritesupremo_eye_fly";
    // 0.05 = ~10,9 blocos/s; 0.12 = ~26 blocos/s (cavalo rapido ~14,5)
    public static final float EYE_FLYING_SPEED = 0.12F;

    public static boolean holdsEye(Player player) {
        return player.getMainHandItem().is(ModItems.DRAGON_EYE.get())
                || player.getOffhandItem().is(ModItems.DRAGON_EYE.get());
    }

    private static void setModifier(Player player, net.minecraft.world.entity.ai.attributes.Attribute attribute,
            UUID id, String name, double amount, AttributeModifier.Operation op, boolean on) {
        AttributeInstance inst = player.getAttribute(attribute);
        if (inst == null) {
            return;
        }
        AttributeModifier existing = inst.getModifier(id);
        if (on && existing == null) {
            inst.addTransientModifier(new AttributeModifier(id, name, amount, op));
        } else if (!on && existing != null) {
            inst.removeModifier(id);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
            return;
        }
        boolean holding = holdsEye(player);
        boolean gauntlet = GauntletEvents.holdsGauntlet(player);
        setModifier(player, Attributes.MAX_HEALTH, HEALTH_ID, "Dragon eye health", 30.0D,
                AttributeModifier.Operation.ADDITION, holding);
        setModifier(player, Attributes.ATTACK_DAMAGE, DAMAGE_ID, "Dragon eye strength", 1.0D,
                AttributeModifier.Operation.MULTIPLY_TOTAL, holding);
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        CompoundTag data = player.getPersistentData();
        if (holding || gauntlet) {
            boolean changed = false;
            if (!player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                changed = true;
            }
            float speed = gauntlet ? GauntletEvents.GAUNTLET_FLYING_SPEED
                    : SupremoEvents.hasSetBonus(player) ? SupremoEvents.NETHERITE_FLYING_SPEED : EYE_FLYING_SPEED;
            if (player.getAbilities().getFlyingSpeed() != speed) {
                player.getAbilities().setFlyingSpeed(speed);
                changed = true;
            }
            data.putBoolean(FLY, true);
            if (changed) {
                player.onUpdateAbilities();
            }
        } else if (data.getBoolean(FLY)) {
            data.putBoolean(FLY, false);
            if (!SupremoEvents.hasSetBonus(player) && !data.getBoolean("netheritesupremo_phasing")) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                player.getAbilities().setFlyingSpeed(SupremoEvents.DEFAULT_FLYING_SPEED);
                player.onUpdateAbilities();
            }
        }
    }

    /** O dragao morto some do mundo com motivo KILLED: nesse momento cai o Olho do Dragao perto do portal. */
    @SubscribeEvent
    public void onDragonRemoved(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof EnderDragon) || !(event.getLevel() instanceof ServerLevel level)
                || entity.getRemovalReason() != Entity.RemovalReason.KILLED) {
            return;
        }
        BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, BlockPos.ZERO);
        ItemEntity drop = new ItemEntity(level, top.getX() + 0.5D, top.getY() + 2.0D, top.getZ() + 0.5D,
                new ItemStack(ModItems.DRAGON_EYE.get()));
        drop.setUnlimitedLifetime();
        drop.setInvulnerable(true);
        level.addFreshEntity(drop);
    }
}
