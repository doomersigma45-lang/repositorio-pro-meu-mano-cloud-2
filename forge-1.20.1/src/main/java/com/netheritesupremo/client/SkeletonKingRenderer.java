package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.entity.SkeletonKing;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Rei Esqueleto: modelo de esqueleto 60% maior; a coroa esta pintada na camada "hat" da textura. */
public class SkeletonKingRenderer extends HumanoidMobRenderer<SkeletonKing, SkeletonModel<SkeletonKing>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/skeleton_king.png");

    public SkeletonKingRenderer(EntityRendererProvider.Context context) {
        super(context, new SkeletonModel<>(context.bakeLayer(ModelLayers.SKELETON)), 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(SkeletonKing king) {
        return TEXTURE;
    }

    @Override
    protected void scale(SkeletonKing king, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.6F, 1.6F, 1.6F);
    }
}
