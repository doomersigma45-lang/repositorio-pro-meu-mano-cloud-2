package com.netheritesupremo.claudecontent.client.renderer;

import com.netheritesupremo.claudecontent.ClaudeContent;
import com.netheritesupremo.claudecontent.entity.CinderStalkerEntity;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Usa o modelo de aranha vanilla (mesma hitbox 1.4 x 0.9) com textura própria. */
public class CinderStalkerRenderer extends MobRenderer<CinderStalkerEntity, SpiderModel<CinderStalkerEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(ClaudeContent.MODID, "textures/entity/cinder_stalker.png");

    public CinderStalkerRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiderModel<>(context.bakeLayer(ModelLayers.SPIDER)), 0.8F);
    }

    @Override
    public ResourceLocation getTextureLocation(CinderStalkerEntity entity) {
        return TEXTURE;
    }
}
