package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.mob.AncientGuardian;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

/** Guardiao Ancestral 1,6x (5 blocos); olhos laranja e rachaduras de magma brilham. */
public class AncientGuardianRenderer extends MobRenderer<AncientGuardian, AncientGuardianModel> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(NetheriteSupremo.MODID, "ancient_guardian"), "main");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/ancient_guardian.png");
    private static final ResourceLocation GLOW =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/ancient_guardian_glow.png");

    public AncientGuardianRenderer(EntityRendererProvider.Context context) {
        super(context, new AncientGuardianModel(context.bakeLayer(LAYER)), 1.6F);
        this.addLayer(new EyesLayer<AncientGuardian, AncientGuardianModel>(this) {
            @Override
            public RenderType renderType() {
                return RenderType.eyes(GLOW);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(AncientGuardian g) {
        return TEXTURE;
    }

    @Override
    protected void scale(AncientGuardian g, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.6F, 1.6F, 1.6F);
    }
}
