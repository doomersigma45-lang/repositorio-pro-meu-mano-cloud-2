package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.mob.Fungus;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class FungusRenderer extends MobRenderer<Fungus, FungusModel> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(NetheriteSupremo.MODID, "fungus"), "main");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/fungus.png");

    public FungusRenderer(EntityRendererProvider.Context context) {
        super(context, new FungusModel(context.bakeLayer(LAYER)), 0.8F);
    }

    @Override
    public ResourceLocation getTextureLocation(Fungus f) {
        return TEXTURE;
    }

    @Override
    protected void scale(Fungus f, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.3F, 1.3F, 1.3F);
    }
}
