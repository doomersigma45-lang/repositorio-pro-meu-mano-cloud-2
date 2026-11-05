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

import com.claudecontent.entity.MosslingEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MosslingRenderer
extends MobRenderer<MosslingEntity, PigModel<MosslingEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("claude_content", "textures/entity/mossling.png");

    public MosslingRenderer(EntityRendererProvider.Context context) {
        super(context, (EntityModel)new PigModel(context.m_174023_(ModelLayers.f_171205_)), 0.5f);
    }

    public ResourceLocation getTextureLocation(MosslingEntity entity) {
        return TEXTURE;
    }
}

