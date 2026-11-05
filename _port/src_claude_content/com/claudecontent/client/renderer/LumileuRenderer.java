/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.model.PigModel
 *  net.minecraft.client.model.geom.ModelLayers
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.MobRenderer
 *  net.minecraft.resources.ResourceLocation
 */
package com.claudecontent.client.renderer;

import com.claudecontent.entity.LumileuEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class LumileuRenderer
extends MobRenderer<LumileuEntity, PigModel<LumileuEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("claude_content", "textures/entity/lumileu.png");

    public LumileuRenderer(EntityRendererProvider.Context context) {
        super(context, (EntityModel)new PigModel(context.m_174023_(ModelLayers.f_171205_)), 0.35f);
    }

    public ResourceLocation getTextureLocation(LumileuEntity entity) {
        return TEXTURE;
    }
}

