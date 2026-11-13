package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.mob.AncientTurtle;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class AncientTurtleRenderer extends MobRenderer<AncientTurtle, AncientTurtleModel> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(NetheriteSupremo.MODID, "ancient_turtle"), "main");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/ancient_turtle.png");
    private static final ResourceLocation GLOW =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/ancient_turtle_glow.png");

    public AncientTurtleRenderer(EntityRendererProvider.Context context) {
        super(context, new AncientTurtleModel(context.bakeLayer(LAYER)), 1.8F);
        if (false) {
            this.addLayer(new EyesLayer<AncientTurtle, AncientTurtleModel>(this) {
                @Override
                public RenderType renderType() {
                    return RenderType.eyes(GLOW);
                }
            });
        }
    }

    @Override
    public ResourceLocation getTextureLocation(AncientTurtle e) {
        return TEXTURE;
    }

    @Override
    protected void scale(AncientTurtle e, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.6F, 1.6F, 1.6F);
    }
}
