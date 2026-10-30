package com.netheritesupremo.client;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.entity.Thief;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Ladrao: 3 skins, escolhida pelo UUID de cada um. */
public class ThiefRenderer extends HumanoidMobRenderer<Thief, PlayerModel<Thief>> {
    private static final ResourceLocation[] TEXTURES = {
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/thief_1.png"),
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/thief_2.png"),
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/thief_3.png")
    };

    public ThiefRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(Thief thief) {
        return TEXTURES[Math.floorMod(thief.getUUID().hashCode(), TEXTURES.length)];
    }
}
