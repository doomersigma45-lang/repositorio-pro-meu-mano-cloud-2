package com.netheritesupremo.claudecontent.client.renderer;

import com.netheritesupremo.claudecontent.ClaudeContent;
import com.netheritesupremo.claudecontent.entity.MosslingEntity;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Usa o modelo de porco vanilla (0.9 x 0.9) com textura própria. */
public class MosslingRenderer extends MobRenderer<MosslingEntity, PigModel<MosslingEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(ClaudeContent.MODID, "textures/entity/mossling.png");

    public MosslingRenderer(EntityRendererProvider.Context context) {
        super(context, new PigModel<>(context.bakeLayer(ModelLayers.PIG)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(MosslingEntity entity) {
        return TEXTURE;
    }
}
