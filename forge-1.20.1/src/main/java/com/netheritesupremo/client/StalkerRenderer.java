package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.mob.Stalker;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

/** Perseguidor 1,3x (3 blocos de altura) com olhos brancos brilhantes. */
public class StalkerRenderer extends MobRenderer<Stalker, StalkerModel> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(NetheriteSupremo.MODID, "stalker"), "main");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/stalker.png");
    private static final ResourceLocation EYES =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/stalker_eyes.png");

    public StalkerRenderer(EntityRendererProvider.Context context) {
        super(context, new StalkerModel(context.bakeLayer(LAYER)), 0.5F);
        this.addLayer(new EyesLayer<Stalker, StalkerModel>(this) {
            @Override
            public RenderType renderType() {
                return RenderType.eyes(EYES);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(Stalker s) {
        return TEXTURE;
    }

    @Override
    protected void scale(Stalker s, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.3F, 1.3F, 1.3F);
    }
}
