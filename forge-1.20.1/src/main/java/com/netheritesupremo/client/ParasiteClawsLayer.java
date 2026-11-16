package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.netheritesupremo.NetheriteSupremo;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

/** Garras compridas e finas saindo das maos (acompanham o movimento dos bracos do modelo humanoide). */
public class ParasiteClawsLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(NetheriteSupremo.MODID, "parasite_claws"), "main");

    private final ModelPart rightClaws;
    private final ModelPart leftClaws;

    public ParasiteClawsLayer(RenderLayerParent<T, M> parent, EntityRendererProvider.Context context) {
        super(parent);
        ModelPart root = context.bakeLayer(LAYER);
        this.rightClaws = root.getChild("right_claws");
        this.leftClaws = root.getChild("left_claws");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        // coordenadas locais do braco: o bloco do braco vai de y=-2 a y=10; as garras saem da ponta (y=10)
        CubeListBuilder right = CubeListBuilder.create().texOffs(0, 0);
        CubeListBuilder left = CubeListBuilder.create().texOffs(0, 0);
        float[] xr = {-3.0F, -1.5F, 0.0F};
        float[] xl = {-1.0F, 0.5F, 2.0F};
        float[] zs = {-1.5F, 0.5F};
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                right.addBox(xr[i], 10.0F, zs[j], 1.0F, 7.0F, 1.0F);
                left.addBox(xl[i], 10.0F, zs[j], 1.0F, 7.0F, 1.0F);
            }
        }
        root.addOrReplaceChild("right_claws", right, PartPose.ZERO);
        root.addOrReplaceChild("left_claws", left, PartPose.ZERO);
        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffer, int light, T entity, float limbSwing,
                       float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isInvisible()) {
            return;
        }
        this.rightClaws.copyFrom(this.getParentModel().rightArm);
        this.leftClaws.copyFrom(this.getParentModel().leftArm);
        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(ParasiteTextures.CLAWS));
        int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);
        this.rightClaws.render(pose, vc, light, overlay);
        this.leftClaws.render(pose, vc, light, overlay);
    }
}
