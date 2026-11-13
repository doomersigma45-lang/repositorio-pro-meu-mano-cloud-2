package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.netheritesupremo.NetheriteSupremo;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;

/** Modelo do golem de ferro, textura de pedra, 0,55x. */
public class StoneGuardianRenderer extends IronGolemRenderer {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/stone_guardian.png");

    public StoneGuardianRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(IronGolem golem) {
        return TEXTURE;
    }

    @Override
    protected void scale(IronGolem golem, PoseStack poseStack, float partialTick) {
        super.scale(golem, poseStack, partialTick);
        poseStack.scale(0.55F, 0.55F, 0.55F);
    }
}
