package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.mob.GiantMosquito;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GiantMosquitoRenderer extends MobRenderer<GiantMosquito, GiantMosquitoModel> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(NetheriteSupremo.MODID, "giant_mosquito"), "main");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/giant_mosquito.png");

    public GiantMosquitoRenderer(EntityRendererProvider.Context context) {
        super(context, new GiantMosquitoModel(context.bakeLayer(LAYER)), 0.8F);
    }

    @Override
    public ResourceLocation getTextureLocation(GiantMosquito m) {
        return TEXTURE;
    }

    @Override
    protected void scale(GiantMosquito m, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.6F, 1.6F, 1.6F);
    }
}
