package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.entity.NetherSpider;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Modelo de aranha com a textura enviada, 30% maior (filhote: metade disso). */
public class NetherSpiderRenderer extends MobRenderer<NetherSpider, SpiderModel<NetherSpider>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/nether_spider.png");

    public NetherSpiderRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiderModel<>(context.bakeLayer(ModelLayers.SPIDER)), 1.0F);
    }

    @Override
    protected float getFlipDegrees(NetherSpider spider) {
        return 180.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(NetherSpider spider) {
        return TEXTURE;
    }

    @Override
    protected void scale(NetherSpider spider, PoseStack poseStack, float partialTick) {
        float scale = spider.isBaby() ? 0.65F : 1.3F;
        poseStack.scale(scale, scale, scale);
    }
}
