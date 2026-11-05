package com.netheritesupremo.claudecontent.client;

import com.netheritesupremo.claudecontent.ClaudeContent;
import com.netheritesupremo.claudecontent.client.renderer.AshReaperRenderer;
import com.netheritesupremo.claudecontent.client.renderer.CinderStalkerRenderer;
import com.netheritesupremo.claudecontent.client.renderer.MosslingRenderer;
import com.netheritesupremo.claudecontent.client.renderer.MossColossusRenderer;
import com.netheritesupremo.claudecontent.client.renderer.LumileuRenderer;
import com.netheritesupremo.claudecontent.registry.ModEntities;
import com.netheritesupremo.claudecontent.registry.ModBlocks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ClaudeContent.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(ModBlocks.ANCIENT_SPROUT.get(), RenderType.cutout()));
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.ASH_REAPER.get(), AshReaperRenderer::new);
        event.registerEntityRenderer(ModEntities.CINDER_STALKER.get(), CinderStalkerRenderer::new);
        event.registerEntityRenderer(ModEntities.MOSSLING.get(), MosslingRenderer::new);
        event.registerEntityRenderer(ModEntities.MOSS_COLOSSUS.get(), MossColossusRenderer::new);
        event.registerEntityRenderer(ModEntities.LUMILEU.get(), LumileuRenderer::new);
    }

    private ClientEvents() {}
}
