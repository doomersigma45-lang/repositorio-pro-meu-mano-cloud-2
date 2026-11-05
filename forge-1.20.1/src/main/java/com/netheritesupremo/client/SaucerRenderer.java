package com.netheritesupremo.client;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.entity.AlienShip;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SaucerRenderer extends MobRenderer<AlienShip, SaucerModel> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(NetheriteSupremo.MODID, "alien_ship"), "main");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/alien_ship.png");

    public SaucerRenderer(EntityRendererProvider.Context context) {
        super(context, new SaucerModel(context.bakeLayer(LAYER)), 1.2F);
    }

    @Override
    public ResourceLocation getTextureLocation(AlienShip ship) {
        return TEXTURE;
    }
}
