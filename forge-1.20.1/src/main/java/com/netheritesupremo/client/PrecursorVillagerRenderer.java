package com.netheritesupremo.client;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.entity.PrecursorVillager;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Aldeao Precursor: 3 skins, escolhida pelo UUID de cada um. */
public class PrecursorVillagerRenderer extends HumanoidMobRenderer<PrecursorVillager, PlayerModel<PrecursorVillager>> {
    private static final ResourceLocation[] TEXTURES = {
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/precursor_villager_1.png"),
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/precursor_villager_2.png"),
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/precursor_villager_3.png")
    };

    private static final ResourceLocation[] HYBRID = {
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/hybrid_villager_1.png"),
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/hybrid_villager_2.png"),
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/hybrid_villager_3.png")
    };

    public PrecursorVillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(PrecursorVillager villager) {
        ResourceLocation[] set = villager.isHybrid() ? HYBRID : TEXTURES;
        return set[Math.floorMod(villager.getUUID().hashCode(), set.length)];
    }
}
