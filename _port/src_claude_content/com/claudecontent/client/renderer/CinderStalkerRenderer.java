/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.model.SpiderModel
 *  net.minecraft.client.model.geom.ModelLayers
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.MobRenderer
 *  net.minecraft.resources.ResourceLocation
 */
package com.claudecontent.client.renderer;

import com.claudecontent.entity.CinderStalkerEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CinderStalkerRenderer
extends MobRenderer<CinderStalkerEntity, SpiderModel<CinderStalkerEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("claude_content", "textures/entity/cinder_stalker.png");

    public CinderStalkerRenderer(EntityRendererProvider.Context context) {
        super(context, (EntityModel)new SpiderModel(context.bakeLayer(ModelLayers.SPIDER)), 0.8f);
    }

    public ResourceLocation getTextureLocation(CinderStalkerEntity entity) {
        return TEXTURE;
    }
}

