package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.mob.AncientWood;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

/** Ancient Wood 3,6x (7 blocos de altura). Olhos dourados brilham. */
public class AncientWoodRenderer extends MobRenderer<AncientWood, WoodStalkerModel<AncientWood>> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(NetheriteSupremo.MODID, "ancient_wood"), "main");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/ancient_wood.png");
    private static final ResourceLocation EYES =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/ancient_wood_eyes.png");

    public AncientWoodRenderer(EntityRendererProvider.Context context) {
        super(context, new WoodStalkerModel<AncientWood>(context.bakeLayer(LAYER)), 3.0F);
        this.addLayer(new EyesLayer<AncientWood, WoodStalkerModel<AncientWood>>(this) {
            @Override
            public RenderType renderType() {
                return RenderType.eyes(EYES);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(AncientWood e) {
        return TEXTURE;
    }

    @Override
    protected void scale(WoodStalker e, PoseStack poseStack, float partialTick) {
        poseStack.scale(3.6F, e.isCrouched() ? 2.0F : 3.6F, 3.6F);
    }
}
