/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.model.geom.ModelLayers
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.MobRenderer
 *  net.minecraft.resources.ResourceLocation
 */
package com.claudecontent.client.renderer;

import com.claudecontent.client.model.ColossusModel;
import com.claudecontent.entity.MossColossusEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MossColossusRenderer
extends MobRenderer<MossColossusEntity, ColossusModel<MossColossusEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("claude_content", "textures/entity/moss_colossus.png");

    public MossColossusRenderer(EntityRendererProvider.Context context) {
        super(context, new ColossusModel(context.bakeLayer(ModelLayers.IRON_GOLEM)), 0.8f);
    }

    protected void scale(MossColossusEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.popPose(1.25f, 1.25f, 1.25f);
    }

    public ResourceLocation getTextureLocation(MossColossusEntity entity) {
        return TEXTURE;
    }
}

