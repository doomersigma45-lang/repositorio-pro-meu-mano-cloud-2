package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.netheritesupremo.mob.ColossalBeetle;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Besouro-rinoceronte (textura 128x128): corpo oval com carapaca, cabeca pequena, grande chifre, 6 patas. */
public class BeetleModel extends EntityModel<ColossalBeetle> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart[] legs = new ModelPart[6];

    public BeetleModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        for (int i = 0; i < 6; i++) {
            this.legs[i] = root.getChild("leg" + i);
        }
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition p = mesh.getRoot();
        p.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-10, -14, -14, 20, 14, 28), PartPose.offset(0, 14, 0));
        p.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 44).addBox(-9, -6, -12, 18, 6, 24), PartPose.offset(0, 0, 0));
        PartDefinition head = p.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 76).addBox(-4, -3, -7, 8, 6, 7),
                PartPose.offset(0, 8, -14));
        PartDefinition horn = head.addOrReplaceChild("horn", CubeListBuilder.create().texOffs(32, 76).addBox(-1.5F, -1.5F, -10, 3, 3, 10),
                PartPose.offsetAndRotation(0, -1, -6, -0.35F, 0, 0));
        horn.addOrReplaceChild("tip", CubeListBuilder.create().texOffs(60, 76).addBox(-1, -6, -1, 2, 6, 2),
                PartPose.offsetAndRotation(0, 0, -10, -0.3F, 0, 0));
        CubeListBuilder leg = CubeListBuilder.create().texOffs(72, 76).addBox(-1.5F, 0, -1.5F, 3, 10, 3);
        float[] z = {-8, 0, 8};
        for (int i = 0; i < 6; i++) {
            boolean left = i % 2 == 1;
            p.addOrReplaceChild("leg" + i, leg, PartPose.offsetAndRotation(left ? 10 : -10, 14, z[i / 2], 0, 0, left ? -0.4F : 0.4F));
        }
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(ColossalBeetle b, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch) {
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);
        for (int i = 0; i < 6; i++) {
            float phase = (i % 2 == 0 ? 0.0F : (float) Math.PI) + i * 0.7F;
            this.legs[i].xRot = Mth.cos(limbSwing * 0.8F + phase) * 0.8F * limbSwingAmount;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
