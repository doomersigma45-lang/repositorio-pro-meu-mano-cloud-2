package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.mob.AncestralGuardian;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

/** Guardiao Ancestral 2,4x (7 blocos); olhos e runas brilham (camada de brilho). */
public class AncestralGuardianRenderer extends MobRenderer<AncestralGuardian, AncestralGuardianModel> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(NetheriteSupremo.MODID, "ancestral_guardian"), "main");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/ancestral_guardian.png");
    private static final ResourceLocation GLOW =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/ancestral_guardian_glow.png");

    public AncestralGuardianRenderer(EntityRendererProvider.Context context) {
        super(context, new AncestralGuardianModel(context.bakeLayer(LAYER)), 2.2F);
        this.addLayer(new EyesLayer<AncestralGuardian, AncestralGuardianModel>(this) {
            @Override
            public RenderType renderType() {
                return RenderType.eyes(GLOW);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(AncestralGuardian g) {
        return TEXTURE;
    }

    @Override
    protected void scale(AncestralGuardian g, PoseStack poseStack, float partialTick) {
        poseStack.scale(2.4F, 2.4F, 2.4F);
    }
}
