package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.mob.TheAbyss;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** O Abismo 2,45x (8 blocos). Nucleo e olhos brilham e pulsam (mais rapido a cada fase). */
public class TheAbyssRenderer extends MobRenderer<TheAbyss, TheAbyssModel> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(NetheriteSupremo.MODID, "the_abyss"), "main");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/the_abyss.png");
    private static final ResourceLocation GLOW =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/the_abyss_glow.png");

    public TheAbyssRenderer(EntityRendererProvider.Context context) {
        super(context, new TheAbyssModel(context.bakeLayer(LAYER)), 3.0F);
        this.addLayer(new RenderLayer<TheAbyss, TheAbyssModel>(this) {
            @Override
            public void render(PoseStack pose, MultiBufferSource buffer, int light, TheAbyss e, float limbSwing, float limbSwingAmount,
                    float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
                float speed = e.phase() >= 3 ? 0.55F : e.phase() == 2 ? 0.32F : 0.18F;
                float k = 0.6F + 0.4F * Mth.sin(ageInTicks * speed);
                VertexConsumer vc = buffer.getBuffer(RenderType.eyes(GLOW));
                this.getParentModel().renderToBuffer(pose, vc, 15728640, OverlayTexture.NO_OVERLAY, k, k, k, 1.0F);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(TheAbyss g) {
        return TEXTURE;
    }

    @Override
    protected void scale(TheAbyss g, PoseStack poseStack, float partialTick) {
        poseStack.scale(2.45F, 2.45F, 2.45F);
    }
}
