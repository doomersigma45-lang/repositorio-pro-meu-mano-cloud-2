/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.model.HumanoidModel
 *  net.minecraft.client.model.geom.ModelLayers
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.HumanoidMobRenderer
 *  net.minecraft.resources.ResourceLocation
 */
package com.claudecontent.client.renderer;

import com.claudecontent.entity.AshReaperEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class AshReaperRenderer
extends HumanoidMobRenderer<AshReaperEntity, HumanoidModel<AshReaperEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("claude_content", "textures/entity/ash_reaper.png");

    public AshReaperRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel(context.m_174023_(ModelLayers.f_171223_)), 0.9f);
    }

    protected void scale(AshReaperEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.m_85841_(1.7f, 1.7f, 1.7f);
    }

    public ResourceLocation getTextureLocation(AshReaperEntity entity) {
        return TEXTURE;
    }
}

