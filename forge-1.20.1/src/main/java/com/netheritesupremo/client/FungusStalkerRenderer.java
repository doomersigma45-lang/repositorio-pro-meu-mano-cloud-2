package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.mob.FungusStalker;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

/** Fungus Stalker 1,55x: chapeu de cogumelo, quatro pernas. */
public class FungusStalkerRenderer extends MobRenderer<FungusStalker, WoodStalkerModel<FungusStalker>> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(NetheriteSupremo.MODID, "fungus_stalker"), "main");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/fungus_stalker.png");
    private static final ResourceLocation EYES =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/fungus_stalker_eyes.png");

    public FungusStalkerRenderer(EntityRendererProvider.Context context) {
        super(context, new WoodStalkerModel<FungusStalker>(context.bakeLayer(LAYER)), 0.7F);
        this.addLayer(new EyesLayer<FungusStalker, WoodStalkerModel<FungusStalker>>(this) {
            @Override
            public RenderType renderType() {
                return RenderType.eyes(EYES);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(FungusStalker e) {
        return TEXTURE;
    }

    @Override
    protected void scale(WoodStalker e, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.55F, e.isCrouched() ? 0.9F : 1.55F, 1.55F);
    }
}
