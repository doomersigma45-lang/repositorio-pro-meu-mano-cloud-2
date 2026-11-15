package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.parasite.AirborneParasite;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Renderer do Totem de Cranios e da Flor parasita (cresce um pouco a cada fase). */
public class AirborneRenderer extends MobRenderer<AirborneParasite, AirborneModel<AirborneParasite>> {
    public static final ModelLayerLocation TOTEM_LAYER = new ModelLayerLocation(new ResourceLocation(NetheriteSupremo.MODID, "parasite_totem"), "main");
    public static final ModelLayerLocation BLOOM_LAYER = new ModelLayerLocation(new ResourceLocation(NetheriteSupremo.MODID, "parasite_bloom"), "main");

    private final ResourceLocation texture;

    public AirborneRenderer(EntityRendererProvider.Context context, ModelLayerLocation layer, ResourceLocation texture, float shadow) {
        super(context, new AirborneModel<AirborneParasite>(context.bakeLayer(layer)), shadow);
        this.texture = texture;
    }

    @Override
    protected void scale(AirborneParasite entity, PoseStack pose, float partialTick) {
        float s = 1.0F + 0.12F * (ParasiteRenderers.phase(entity) - 1);
        pose.scale(s, s, s);
    }

    @Override
    public ResourceLocation getTextureLocation(AirborneParasite entity) {
        return this.texture;
    }
}
