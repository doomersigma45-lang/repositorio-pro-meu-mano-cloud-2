package com.netheritesupremo.mob;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.registry.ModEntities;
import com.netheritesupremo.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Maca de netherite numa galinha comum a transforma em galinha de netherite. */
@Mod.EventBusSubscriber(modid = NetheriteSupremo.MODID)
public final class MobEvents {
    private MobEvents() {
    }

    /** O Abismo so existe na Terra Negra: em qualquer outra dimensao ele nao entra no mundo. */
    @SubscribeEvent
    public static void onJoin(net.minecraftforge.event.entity.EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof TheAbyss && !event.getLevel().isClientSide()
                && event.getLevel().dimension() != com.netheritesupremo.blackrealm.BlackRealm.KEY) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Chicken chicken) || chicken.getType() != EntityType.CHICKEN || chicken.isBaby()) {
            return;
        }
        ItemStack stack = event.getItemStack();
        if (!stack.is(ModItems.NETHERITE_APPLE.get()) && !stack.is(ModItems.ENCHANTED_NETHERITE_APPLE.get())) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        NetheriteChicken nc = ModEntities.NETHERITE_CHICKEN.get().create(level);
        if (nc == null) {
            return;
        }
        nc.moveTo(chicken.getX(), chicken.getY(), chicken.getZ(), chicken.getYRot(), chicken.getXRot());
        nc.setYBodyRot(chicken.yBodyRot);
        nc.setYHeadRot(chicken.getYHeadRot());
        if (chicken.hasCustomName()) {
            nc.setCustomName(chicken.getCustomName());
            nc.setCustomNameVisible(chicken.isCustomNameVisible());
        }
        nc.setPersistenceRequired();
        if (!event.getEntity().getAbilities().instabuild) {
            stack.shrink(1);
        }
        chicken.discard();
        level.addFreshEntity(nc);
        level.sendParticles(ParticleTypes.FLAME, nc.getX(), nc.getY() + 0.5D, nc.getZ(), 25, 0.3D, 0.4D, 0.3D, 0.05D);
        level.playSound(null, nc.blockPosition(), SoundEvents.ANVIL_USE, SoundSource.NEUTRAL, 0.8F, 1.2F);
    }
}
