package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.netheritesupremo.mob.Stalker;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Perseguidor (64x64): humanoide magro, cabeca pequena, bracos longos com 3 pontas, pernas invertidas. */
public class StalkerModel extends EntityModel<Stalker> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public StalkerModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition p = mesh.getRoot();
        p.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2, -4, -2, 4, 4, 4), PartPose.offset(0, -9, 0));
        p.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 10).addBox(-2, 0, -1, 4, 14, 2), PartPose.offset(0, -9, 0));
        CubeListBuilder arm = CubeListBuilder.create().texOffs(14, 10).addBox(-1, 0, -1, 2, 20, 2)
                .texOffs(44, 10).addBox(-1, 20, -0.5F, 0.6F, 5, 0.6F)
                .addBox(-0.3F, 20, -0.5F, 0.6F, 5, 0.6F)
                .addBox(0.4F, 20, -0.5F, 0.6F, 5, 0.6F);
        p.addOrReplaceChild("right_arm", arm, PartPose.offset(-3, -8, 0));
        p.addOrReplaceChild("left_arm", arm, PartPose.offset(3, -8, 0));
        for (String side : new String[] {"right_leg", "left_leg"}) {
            float x = side.startsWith("right") ? -1.5F : 1.5F;
            PartDefinition thigh = p.addOrReplaceChild(side, CubeListBuilder.create().texOffs(24, 10).addBox(-1, 0, -1, 2, 11, 2),
                    PartPose.offset(x, 5, 0));
            thigh.addOrReplaceChild("shin", CubeListBuilder.create().texOffs(34, 10).addBox(-1, 0, -1, 2, 11, 2),
                    PartPose.offsetAndRotation(0, 11, 0, -1.0F, 0, 0));
        }
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(Stalker s, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch) {
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);
        float a = Mth.cos(limbSwing * 0.6662F) * 1.2F * limbSwingAmount;
        this.rightLeg.xRot = 0.5F + a;
        this.leftLeg.xRot = 0.5F - a;
        if (s.isAggressive()) {
            this.rightArm.xRot = -1.5F + Mth.sin(ageInTicks * 0.3F) * 0.1F;
            this.leftArm.xRot = -1.5F - Mth.sin(ageInTicks * 0.3F) * 0.1F;
        } else {
            this.rightArm.xRot = -a * 0.8F;
            this.leftArm.xRot = a * 0.8F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
