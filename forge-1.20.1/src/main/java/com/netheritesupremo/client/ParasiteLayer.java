package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

/** Camada verde translucida por cima do modelo original: da o aspecto "infectado" a versao parasita. */
public class ParasiteLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private final ResourceLocation texture;

    public ParasiteLayer(RenderLayerParent<T, M> parent, ResourceLocation texture) {
        super(parent);
        this.texture = texture;
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffer, int light, T entity, float limbSwing,
                       float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isInvisible()) {
            return;
        }
        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucent(this.texture));
        this.getParentModel().renderToBuffer(pose, vc, light, LivingEntityRenderer.getOverlayCoords(entity, 0.0F),
                0.25F, 0.95F, 0.3F, 0.62F);
    }
}
