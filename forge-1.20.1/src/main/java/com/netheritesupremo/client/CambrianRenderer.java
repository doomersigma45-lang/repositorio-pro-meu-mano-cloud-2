package com.netheritesupremo.client;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.entity.CambrianCreature;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Renderizador dos animais do Cambriano (modelo segmentado + textura propria). */
public class CambrianRenderer<T extends CambrianCreature> extends MobRenderer<T, SegmentedModel<T>> {
    public static final ModelLayerLocation ANOMALOCARIS_LAYER = layer("anomalocaris");
    public static final ModelLayerLocation TRILOBITE_LAYER = layer("trilobite");
    public static final ModelLayerLocation OPABINIA_LAYER = layer("opabinia");

    private final ResourceLocation texture;

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(new ResourceLocation(NetheriteSupremo.MODID, name), "main");
    }

    public CambrianRenderer(EntityRendererProvider.Context context, ModelLayerLocation layer,
            SegmentedModel.Spec spec, String textureName, float shadow) {
        super(context, new SegmentedModel<>(context.bakeLayer(layer), spec), shadow);
        this.texture = new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/" + textureName + ".png");
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return this.texture;
    }
}
