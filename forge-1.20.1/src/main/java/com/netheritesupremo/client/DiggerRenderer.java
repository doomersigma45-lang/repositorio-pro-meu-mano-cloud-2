package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.mob.Digger;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class DiggerRenderer extends MobRenderer<Digger, DiggerModel> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(NetheriteSupremo.MODID, "digger"), "main");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/digger.png");
    private static final ResourceLocation GLOW =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/digger_glow.png");

    public DiggerRenderer(EntityRendererProvider.Context context) {
        super(context, new DiggerModel(context.bakeLayer(LAYER)), 0.6F);
        if (false) {
            this.addLayer(new EyesLayer<Digger, DiggerModel>(this) {
                @Override
                public RenderType renderType() {
                    return RenderType.eyes(GLOW);
                }
            });
        }
    }

    @Override
    public ResourceLocation getTextureLocation(Digger e) {
        return TEXTURE;
    }

    @Override
    protected void scale(Digger e, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.3F, 1.3F, 1.3F);
    }
}
