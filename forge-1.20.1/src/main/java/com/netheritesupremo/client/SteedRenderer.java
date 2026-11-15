package com.netheritesupremo.client;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.UndeadHorseRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

/** Montaria esqueletica do Cavaleiro: o renderer vanilla so conhece os cavalos vanilla, entao fixamos a textura. */
public class SteedRenderer extends UndeadHorseRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/horse/horse_skeleton.png");

    public SteedRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.SKELETON_HORSE);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractHorse horse) {
        return TEXTURE;
    }
}
