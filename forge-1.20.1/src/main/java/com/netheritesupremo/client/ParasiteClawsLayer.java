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

    /** Garra: base vermelha escura e ponta de osso inclinada para frente. xs = posicoes em x; z fixo por garra. */
    private static void claw(PartDefinition arm, String name, float x, float z, float zRot) {
        PartDefinition c = arm.addOrReplaceChild(name, CubeListBuilder.create().texOffs(0, 0)
                .addBox(-0.5F, 0.0F, -0.5F, 1.0F, 3.5F, 1.0F), PartPose.offsetAndRotation(x, 10.0F, z, -0.2F, 0.0F, zRot));
        c.addOrReplaceChild(name + "_tip", CubeListBuilder.create().texOffs(0, 8)
                .addBox(-0.3F, 0.0F, -0.3F, 0.6F, 3.5F, 0.6F), PartPose.offsetAndRotation(0.0F, 3.5F, 0.0F, -0.5F, 0.0F, 0.0F));
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        // coordenadas locais do braco: o bloco vai de y=-2 a y=10; as garras saem da ponta (y=10). Direito: x -3..1.
        PartDefinition right = root.addOrReplaceChild("right_claws", CubeListBuilder.create(), PartPose.ZERO);
        PartDefinition left = root.addOrReplaceChild("left_claws", CubeListBuilder.create(), PartPose.ZERO);
        float[] xr = {-2.4F, -1.0F, 0.4F};
        for (int i = 0; i < 3; i++) {
            claw(right, "r" + i, xr[i], -1.2F, -0.05F * (i - 1));
            claw(left, "l" + i, -xr[i], -1.2F, 0.05F * (i - 1));
        }
        claw(right, "r3", -1.7F, 1.2F, 0.0F);
        claw(right, "r4", -0.3F, 1.2F, 0.0F);
        claw(left, "l3", 1.7F, 1.2F, 0.0F);
        claw(left, "l4", 0.3F, 1.2F, 0.0F);
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
