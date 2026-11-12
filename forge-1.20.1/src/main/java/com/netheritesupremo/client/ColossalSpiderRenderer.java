package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.mob.ColossalSpider;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ColossalSpiderRenderer extends MobRenderer<ColossalSpider, ColossalSpiderModel> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(NetheriteSupremo.MODID, "colossal_spider"), "main");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/colossal_spider.png");

    public ColossalSpiderRenderer(EntityRendererProvider.Context context) {
        super(context, new ColossalSpiderModel(context.bakeLayer(LAYER)), 1.8F);
    }

    @Override
    protected float getFlipDegrees(ColossalSpider spider) {
        return 180.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(ColossalSpider spider) {
        return TEXTURE;
    }

    @Override
    protected void scale(ColossalSpider spider, PoseStack poseStack, float partialTick) {
        poseStack.scale(2.2F, 2.2F, 2.2F);
    }
}
