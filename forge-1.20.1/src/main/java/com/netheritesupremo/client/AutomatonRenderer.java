package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.mob.Automaton;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class AutomatonRenderer extends MobRenderer<Automaton, AutomatonModel> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(NetheriteSupremo.MODID, "automaton"), "main");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/automaton.png");
    private static final ResourceLocation GLOW =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/automaton_glow.png");

    public AutomatonRenderer(EntityRendererProvider.Context context) {
        super(context, new AutomatonModel(context.bakeLayer(LAYER)), 0.9F);
        if (true) {
            this.addLayer(new EyesLayer<Automaton, AutomatonModel>(this) {
                @Override
                public RenderType renderType() {
                    return RenderType.eyes(GLOW);
                }
            });
        }
    }

    @Override
    public ResourceLocation getTextureLocation(Automaton e) {
        return TEXTURE;
    }

    @Override
    protected void scale(Automaton e, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.4F, 1.4F, 1.4F);
    }
}
