package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.mob.Goose;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GooseRenderer extends MobRenderer<Goose, GooseModel> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(NetheriteSupremo.MODID, "goose"), "main");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/goose.png");

    public GooseRenderer(EntityRendererProvider.Context context) {
        super(context, new GooseModel(context.bakeLayer(LAYER)), 0.35F);
    }

    @Override
    public ResourceLocation getTextureLocation(Goose goose) {
        return TEXTURE;
    }

    @Override
    protected void scale(Goose goose, PoseStack poseStack, float partialTick) {
        float s = goose.isBaby() ? 0.5F : 1.0F;
        poseStack.scale(s, s, s);
    }
}
