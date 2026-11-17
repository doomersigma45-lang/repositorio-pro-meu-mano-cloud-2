package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.netheritesupremo.parasite.ParasiteWarden;
import net.minecraft.client.model.WardenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

/** Warden infectado: modelo do Warden com textura de carne e brilho vermelho (sem os brilhos azuis do original). */
public class ParasiteWardenRenderer extends MobRenderer<ParasiteWarden, WardenModel<ParasiteWarden>> {
    public ParasiteWardenRenderer(EntityRendererProvider.Context context) {
        super(context, new WardenModel<ParasiteWarden>(context.bakeLayer(ModelLayers.WARDEN)), 0.9F);
        this.addLayer(new EyesLayer<ParasiteWarden, WardenModel<ParasiteWarden>>(this) {
            @Override
            public RenderType renderType() {
                return RenderType.eyes(ParasiteTextures.WARDEN_GLOW);
            }
        });
    }

    @Override
    protected void scale(ParasiteWarden entity, PoseStack pose, float partialTick) {
        super.scale(entity, pose, partialTick);
        ParasiteRenderers.deform(pose, ParasiteRenderers.phase(entity), entity, partialTick);
    }

    @Override
    public ResourceLocation getTextureLocation(ParasiteWarden entity) {
        return ParasiteTextures.WARDEN;
    }
}
