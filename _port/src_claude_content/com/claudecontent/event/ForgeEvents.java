/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.claudecontent.event;

import com.claudecontent.item.CinderArmorItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="claude_content", bus=Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeEvents {
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity attacker;
        LivingEntity victim = event.getEntity();
        if (victim.level().isClientSide) {
            return;
        }
        Entity entity = event.getSource().getEntity();
        if (entity instanceof LivingEntity && (attacker = (LivingEntity)entity) != victim && CinderArmorItem.hasFullSet(victim) && victim.getRandom().nextFloat() < 0.25f) {
            attacker.getRandomZ(4);
        }
    }

    private ForgeEvents() {
    }
}

