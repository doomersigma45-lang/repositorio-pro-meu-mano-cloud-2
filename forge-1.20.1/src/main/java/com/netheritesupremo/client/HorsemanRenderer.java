package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.netheritesupremo.NetheriteSupremo;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;

/** Cavaleiro sem Cabeca (1,25x) e o filhote (0,6x): esqueleto preto, detalhes verdes brilhantes, abobora na cabeca. */
public class HorsemanRenderer<T extends Mob & RangedAttackMob> extends HumanoidMobRenderer<T, HorsemanModel<T>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/headless_horseman.png");
    private static final ResourceLocation GLOW =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/headless_horseman_glow.png");
    private final float scale;

    public HorsemanRenderer(EntityRendererProvider.Context context, float scale) {
        super(context, new HorsemanModel<T>(context.bakeLayer(ModelLayers.SKELETON)), 0.5F * scale);
        this.scale = scale;
        this.addLayer(new HumanoidArmorLayer<T, HorsemanModel<T>, SkeletonModel<T>>(this,
                new SkeletonModel<T>(context.bakeLayer(ModelLayers.SKELETON_INNER_ARMOR)),
                new SkeletonModel<T>(context.bakeLayer(ModelLayers.SKELETON_OUTER_ARMOR)), context.getModelManager()));
        this.addLayer(new EyesLayer<T, HorsemanModel<T>>(this) {
            @Override
            public RenderType renderType() {
                return RenderType.eyes(GLOW);
            }
        });
    }

    @Override
    protected void scale(T mob, PoseStack poseStack, float partialTick) {
        poseStack.scale(this.scale, this.scale, this.scale);
    }

    @Override
    public ResourceLocation getTextureLocation(T mob) {
        return TEXTURE;
    }
}
