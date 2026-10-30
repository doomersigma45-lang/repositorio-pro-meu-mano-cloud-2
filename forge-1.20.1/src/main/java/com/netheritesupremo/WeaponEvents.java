package com.netheritesupremo;

import com.netheritesupremo.registry.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** Katana: dobro de dano em bosses (tag forge:bosses, que o mod estende com seus proprios bosses). */
public class WeaponEvents {
    @SubscribeEvent
    public void onHurt(LivingHurtEvent event) {
        if (event.getSource().getDirectEntity() instanceof Player player
                && player.getMainHandItem().is(ModItems.KATANA.get())
                && event.getEntity().getType().is(Tags.EntityTypes.BOSSES)) {
            event.setAmount(event.getAmount() * 2.0F);
        }
    }
}
