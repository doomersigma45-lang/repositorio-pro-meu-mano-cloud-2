package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

/** Renderer dos parasitas hibridos: modelo proprio, textura gerada, olhos brilhantes e deformacao das fases. */
public class ParasiteBeastRenderer<T extends Mob> extends MobRenderer<T, EntityModel<T>> {
    private final ResourceLocation texture;
    private final float size;

    public ParasiteBeastRenderer(EntityRendererProvider.Context ctx, EntityModel<T> model, float shadow, ResourceLocation texture, float size) {
        super(ctx, model, shadow);
        this.texture = texture;
        this.size = size;
        this.addLayer(new EyesLayer<T, EntityModel<T>>(this) {
            @Override
            public RenderType renderType() {
                return RenderType.eyes(ParasiteTextures.HYBRID_GLOW);
            }
        });
    }

    @Override
    protected void scale(T entity, PoseStack pose, float partialTick) {
        pose.scale(this.size, this.size, this.size);
        ParasiteRenderers.deform(pose, ParasiteRenderers.phase(entity), entity, partialTick);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return this.texture;
    }
}
