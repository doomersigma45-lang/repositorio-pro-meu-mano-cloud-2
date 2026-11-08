package com.netheritesupremo.client;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.skeleton.SkeletonVillager;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Esqueleto villager: modelo de esqueleto com a textura de terno enviada pelo usuario. */
public class SkeletonVillagerRenderer extends HumanoidMobRenderer<SkeletonVillager, SkeletonModel<SkeletonVillager>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/skeleton_villager.png");

    public SkeletonVillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new SkeletonModel<>(context.bakeLayer(ModelLayers.SKELETON)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(SkeletonVillager villager) {
        return TEXTURE;
    }
}
