package com.netheritesupremo.claudecontent.client.renderer;

import com.netheritesupremo.claudecontent.ClaudeContent;
import com.netheritesupremo.claudecontent.entity.AshReaperEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Reaproveita o modelo humanoide vanilla, ampliado, com textura própria. */
public class AshReaperRenderer extends HumanoidMobRenderer<AshReaperEntity, HumanoidModel<AshReaperEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(ClaudeContent.MODID, "textures/entity/ash_reaper.png");

    public AshReaperRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.9F);
    }

    @Override
    protected void scale(AshReaperEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.7F, 1.7F, 1.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(AshReaperEntity entity) {
        return TEXTURE;
    }
}
