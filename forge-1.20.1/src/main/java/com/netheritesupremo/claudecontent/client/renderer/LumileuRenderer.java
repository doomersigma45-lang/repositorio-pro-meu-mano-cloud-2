package com.netheritesupremo.claudecontent.client.renderer;

import com.netheritesupremo.claudecontent.ClaudeContent;
import com.netheritesupremo.claudecontent.entity.LumileuEntity;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class LumileuRenderer extends MobRenderer<LumileuEntity, PigModel<LumileuEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ClaudeContent.MODID, "textures/entity/lumileu.png");
    public LumileuRenderer(EntityRendererProvider.Context context) {
        super(context, new PigModel<>(context.bakeLayer(ModelLayers.PIG)), 0.35F);
    }
    @Override public ResourceLocation getTextureLocation(LumileuEntity entity) { return TEXTURE; }
}
