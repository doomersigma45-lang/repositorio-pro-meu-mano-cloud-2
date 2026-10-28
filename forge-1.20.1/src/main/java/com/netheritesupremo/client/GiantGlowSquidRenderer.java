package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SquidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.GlowSquidRenderer;
import net.minecraft.world.entity.animal.GlowSquid;

/** Lula brilhante de verdade (modelo/textura do jogo), so que enorme. */
public class GiantGlowSquidRenderer extends GlowSquidRenderer {
    private final float size;

    public GiantGlowSquidRenderer(EntityRendererProvider.Context ctx, float size) {
        super(ctx, new SquidModel<>(ctx.bakeLayer(ModelLayers.GLOW_SQUID)));
        this.size = size;
        this.shadowRadius = 0.5F * size;
    }

    @Override
    protected void scale(GlowSquid squid, PoseStack stack, float partialTick) {
        super.scale(squid, stack, partialTick);
        stack.scale(this.size, this.size, this.size);
    }
}
