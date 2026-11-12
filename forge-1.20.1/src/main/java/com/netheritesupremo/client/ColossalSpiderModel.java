package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.netheritesupremo.mob.ColossalSpider;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Aranha com espinhos nas patas e nas costas (textura 64x64); animacao das patas igual a da aranha comum. */
public class ColossalSpiderModel extends EntityModel<ColossalSpider> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart[] legs = new ModelPart[8];

    public ColossalSpiderModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        for (int i = 0; i < 8; i++) {
            this.legs[i] = root.getChild("leg" + i);
        }
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition p = mesh.getRoot();
        p.addOrReplaceChild("head", CubeListBuilder.create().texOffs(32, 4).addBox(-4, -4, -8, 8, 8, 8), PartPose.offset(0, 15, -3));
        p.addOrReplaceChild("body0", CubeListBuilder.create().texOffs(0, 0).addBox(-3, -3, -3, 6, 6, 6), PartPose.offset(0, 15, 0));
        p.addOrReplaceChild("body1", CubeListBuilder.create().texOffs(0, 12).addBox(-5, -4, -6, 10, 8, 12)
                .texOffs(0, 40).addBox(-1, -8, -4, 2, 4, 2)
                .addBox(-1, -9, 0, 2, 5, 2)
                .addBox(-1, -8, 4, 2, 4, 2)
                .addBox(-4, -7, -1, 2, 3, 2)
                .addBox(2, -7, -1, 2, 3, 2), PartPose.offset(0, 15, 9));
        CubeListBuilder right = CubeListBuilder.create().texOffs(18, 0).addBox(-15, -1, -1, 16, 2, 2)
                .texOffs(0, 32).addBox(-12, -3, -0.5F, 1, 2, 1).addBox(-8, -3, -0.5F, 1, 2, 1).addBox(-4, -3, -0.5F, 1, 2, 1);
        CubeListBuilder left = CubeListBuilder.create().texOffs(18, 0).mirror().addBox(-1, -1, -1, 16, 2, 2)
                .texOffs(0, 32).addBox(11, -3, -0.5F, 1, 2, 1).addBox(7, -3, -0.5F, 1, 2, 1).addBox(3, -3, -0.5F, 1, 2, 1);
        float[] z = {2, 2, 1, 1, 0, 0, -1, -1};
        for (int i = 0; i < 8; i++) {
            boolean isLeft = (i % 2) == 1;
            p.addOrReplaceChild("leg" + i, isLeft ? left : right, PartPose.offset(isLeft ? 4 : -4, 15, z[i]));
        }
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(ColossalSpider spider, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch) {
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);
        float f = (float) Math.PI / 4F;
        float[] zr = {-f, f, -f * 0.74F, f * 0.74F, -f * 0.74F, f * 0.74F, -f, f};
        float f2 = (float) Math.PI / 8F;
        float[] yr = {f2 * 2, -f2 * 2, f2, -f2, -f2, f2, -f2 * 2, f2 * 2};
        float c0 = -(Mth.cos(limbSwing * 1.3324F) * 0.4F) * limbSwingAmount;
        float c1 = -(Mth.cos(limbSwing * 1.3324F + (float) Math.PI) * 0.4F) * limbSwingAmount;
        float c2 = -(Mth.cos(limbSwing * 1.3324F + (float) Math.PI / 2F) * 0.4F) * limbSwingAmount;
        float c3 = -(Mth.cos(limbSwing * 1.3324F + 4.712389F) * 0.4F) * limbSwingAmount;
        float s0 = Math.abs(Mth.sin(limbSwing * 0.6662F) * 0.4F) * limbSwingAmount;
        float s1 = Math.abs(Mth.sin(limbSwing * 0.6662F + (float) Math.PI) * 0.4F) * limbSwingAmount;
        float s2 = Math.abs(Mth.sin(limbSwing * 0.6662F + (float) Math.PI / 2F) * 0.4F) * limbSwingAmount;
        float s3 = Math.abs(Mth.sin(limbSwing * 0.6662F + 4.712389F) * 0.4F) * limbSwingAmount;
        float[] ya = {c0, -c0, c1, -c1, c2, -c2, c3, -c3};
        float[] za = {s0, -s0, s1, -s1, s2, -s2, s3, -s3};
        for (int i = 0; i < 8; i++) {
            this.legs[i].zRot = zr[i] + za[i];
            this.legs[i].yRot = yr[i] + ya[i];
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
