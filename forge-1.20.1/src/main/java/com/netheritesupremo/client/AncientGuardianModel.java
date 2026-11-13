package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.netheritesupremo.mob.AncientGuardian;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Guardiao Ancestral (128x128): bipede enorme, cabeca com 2 chifres, 4 bracos (2 grandes, 2 menores), pernas de animal. */
public class AncientGuardianModel extends EntityModel<AncientGuardian> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart bigR;
    private final ModelPart bigL;
    private final ModelPart smallR;
    private final ModelPart smallL;
    private final ModelPart legR;
    private final ModelPart legL;

    public AncientGuardianModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.bigR = root.getChild("big_right");
        this.bigL = root.getChild("big_left");
        this.smallR = root.getChild("small_right");
        this.smallL = root.getChild("small_left");
        this.legR = root.getChild("leg_right");
        this.legL = root.getChild("leg_left");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition p = mesh.getRoot();
        p.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 24).addBox(-8, -20, -4, 16, 20, 9), PartPose.offset(0, 8, 0));
        PartDefinition head = p.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5, -9, -5, 10, 9, 10),
                PartPose.offset(0, -12, 0));
        for (int s = -1; s <= 1; s += 2) {
            PartDefinition horn = head.addOrReplaceChild(s < 0 ? "horn_r" : "horn_l",
                    CubeListBuilder.create().texOffs(44, 0).addBox(-1.5F, -6, -1.5F, 3, 6, 3),
                    PartPose.offsetAndRotation(5 * s, -7, 0, 0, 0, 0.6F * s));
            horn.addOrReplaceChild(s < 0 ? "tip_r" : "tip_l", CubeListBuilder.create().texOffs(60, 0).addBox(-1, -5, -1, 2, 5, 2),
                    PartPose.offsetAndRotation(0, -6, 0, 0, 0, -0.9F * s));
        }
        CubeListBuilder big = CubeListBuilder.create().texOffs(0, 56).addBox(-3.5F, 0, -3.5F, 7, 20, 7)
                .texOffs(30, 56).addBox(-4, 20, -4, 8, 6, 8);
        p.addOrReplaceChild("big_right", big, PartPose.offset(-11, -10, 0));
        p.addOrReplaceChild("big_left", big, PartPose.offset(11, -10, 0));
        CubeListBuilder small = CubeListBuilder.create().texOffs(64, 56).addBox(-2, 0, -2, 4, 12, 4);
        p.addOrReplaceChild("small_right", small, PartPose.offsetAndRotation(-9, -3, -3, -0.6F, 0, 0));
        p.addOrReplaceChild("small_left", small, PartPose.offsetAndRotation(9, -3, -3, -0.6F, 0, 0));
        for (int s = -1; s <= 1; s += 2) {
            PartDefinition thigh = p.addOrReplaceChild(s < 0 ? "leg_right" : "leg_left",
                    CubeListBuilder.create().texOffs(0, 86).addBox(-3, 0, -3, 6, 9, 6), PartPose.offsetAndRotation(5 * s, 8, 0, 0.5F, 0, 0));
            PartDefinition shin = thigh.addOrReplaceChild("shin", CubeListBuilder.create().texOffs(30, 86).addBox(-2.5F, 0, -2.5F, 5, 9, 5),
                    PartPose.offsetAndRotation(0, 9, 0, -1.0F, 0, 0));
            shin.addOrReplaceChild("foot", CubeListBuilder.create().texOffs(56, 86).addBox(-3, 6, -6, 6, 3, 8),
                    PartPose.offsetAndRotation(0, 0, 0, 0.5F, 0, 0));
        }
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(AncientGuardian g, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch) {
        if (g.isDormant()) {
            this.head.xRot = 0.6F;
            this.head.yRot = 0.0F;
            this.bigR.xRot = this.bigL.xRot = 0.1F;
            this.smallR.xRot = this.smallL.xRot = -0.2F;
            this.legR.xRot = this.legL.xRot = 0.5F;
            return;
        }
        this.head.xRot = headPitch * ((float) Math.PI / 180F);
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        float a = Mth.cos(limbSwing * 0.5F) * 0.9F * limbSwingAmount;
        this.legR.xRot = 0.5F + a;
        this.legL.xRot = 0.5F - a;
        if (g.isAggressive()) {
            this.bigR.xRot = -1.6F + Mth.sin(ageInTicks * 0.2F) * 0.1F;
            this.bigL.xRot = -1.6F - Mth.sin(ageInTicks * 0.2F) * 0.1F;
        } else {
            this.bigR.xRot = -a * 0.7F;
            this.bigL.xRot = a * 0.7F;
        }
        this.smallR.xRot = -0.6F + a * 0.5F;
        this.smallL.xRot = -0.6F - a * 0.5F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
