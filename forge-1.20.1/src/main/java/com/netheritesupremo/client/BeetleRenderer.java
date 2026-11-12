package com.netheritesupremo.client;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.mob.ColossalBeetle;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BeetleRenderer extends MobRenderer<ColossalBeetle, BeetleModel> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(NetheriteSupremo.MODID, "colossal_beetle"), "main");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/colossal_beetle.png");

    public BeetleRenderer(EntityRendererProvider.Context context) {
        super(context, new BeetleModel(context.bakeLayer(LAYER)), 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(ColossalBeetle b) {
        return TEXTURE;
    }
}
