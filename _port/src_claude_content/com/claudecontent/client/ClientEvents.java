/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.ItemBlockRenderTypes
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.block.Block
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.EntityRenderersEvent$RegisterRenderers
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 */
package com.claudecontent.client;

import com.claudecontent.client.renderer.AshReaperRenderer;
import com.claudecontent.client.renderer.CinderStalkerRenderer;
import com.claudecontent.client.renderer.LumileuRenderer;
import com.claudecontent.client.renderer.MossColossusRenderer;
import com.claudecontent.client.renderer.MosslingRenderer;
import com.claudecontent.registry.ModBlocks;
import com.claudecontent.registry.ModEntities;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid="claude_content", bus=Mod.EventBusSubscriber.Bus.MOD, value={Dist.CLIENT})
public final class ClientEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer((Block)((Block)ModBlocks.ANCIENT_SPROUT.get()), (RenderType)RenderType.lambda$static$3()));
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer((EntityType)ModEntities.ASH_REAPER.get(), AshReaperRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.CINDER_STALKER.get(), CinderStalkerRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.MOSSLING.get(), MosslingRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.MOSS_COLOSSUS.get(), MossColossusRenderer::new);
        event.registerEntityRenderer((EntityType)ModEntities.LUMILEU.get(), LumileuRenderer::new);
    }

    private ClientEvents() {
    }
}

