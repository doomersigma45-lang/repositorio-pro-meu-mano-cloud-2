package com.netheritesupremo.claudecontent.client.renderer;

import com.netheritesupremo.claudecontent.ClaudeContent;
import com.netheritesupremo.claudecontent.client.model.ColossusModel;
import com.netheritesupremo.claudecontent.entity.MossColossusEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MossColossusRenderer extends MobRenderer<MossColossusEntity, ColossusModel<MossColossusEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ClaudeContent.MODID, "textures/entity/moss_colossus.png");
    public MossColossusRenderer(EntityRendererProvider.Context context) {
        super(context, new ColossusModel<>(context.bakeLayer(ModelLayers.IRON_GOLEM)), 0.8F);
    }
    @Override protected void scale(MossColossusEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.25F, 1.25F, 1.25F);
    }
    @Override public ResourceLocation getTextureLocation(MossColossusEntity entity) { return TEXTURE; }
}
