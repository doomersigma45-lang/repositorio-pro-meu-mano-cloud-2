package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.entity.Necromancer;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Desenha o necromante com o modelo de esqueleto e a textura enviada pelo usuario. */
public class NecromancerRenderer extends HumanoidMobRenderer<Necromancer, SkeletonModel<Necromancer>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/necromancer.png");

    public NecromancerRenderer(EntityRendererProvider.Context context) {
        super(context, new SkeletonModel<>(context.bakeLayer(ModelLayers.SKELETON)), 0.6F);
    }

    @Override
    public ResourceLocation getTextureLocation(Necromancer necromancer) {
        return TEXTURE;
    }

    @Override
    protected void scale(Necromancer necromancer, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.2F, 1.2F, 1.2F);
    }
}
