package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.mob.WoodStalker;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

/** Wood Stalker 1,55x (3 blocos de altura em pe; agachado fica achatado). Olhos verde-palido brilham. */
public class WoodStalkerRenderer extends MobRenderer<WoodStalker, WoodStalkerModel> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(NetheriteSupremo.MODID, "wood_stalker"), "main");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/wood_stalker.png");
    private static final ResourceLocation EYES =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/wood_stalker_eyes.png");

    public WoodStalkerRenderer(EntityRendererProvider.Context context) {
        super(context, new WoodStalkerModel(context.bakeLayer(LAYER)), 0.5F);
        this.addLayer(new EyesLayer<WoodStalker, WoodStalkerModel>(this) {
            @Override
            public RenderType renderType() {
                return RenderType.eyes(EYES);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(WoodStalker e) {
        return TEXTURE;
    }

    @Override
    protected void scale(WoodStalker e, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.55F, e.isCrouched() ? 0.9F : 1.55F, 1.55F);
    }
}
