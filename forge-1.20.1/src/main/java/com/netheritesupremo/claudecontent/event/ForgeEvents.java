package com.netheritesupremo.claudecontent.event;

import com.netheritesupremo.claudecontent.ClaudeContent;
import com.netheritesupremo.claudecontent.item.CinderArmorItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Eventos do barramento do Forge (bônus do conjunto completo da Armadura de Brasa). */
@Mod.EventBusSubscriber(modid = ClaudeContent.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeEvents {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide) {
            return;
        }
        if (event.getSource().getEntity() instanceof LivingEntity attacker
                && attacker != victim
                && CinderArmorItem.hasFullSet(victim)
                && victim.getRandom().nextFloat() < 0.25F) {
            attacker.setSecondsOnFire(4);
        }
    }

    private ForgeEvents() {}
}
