package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.netheritesupremo.mob.WoodStalker;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Wood Stalker (64x64): humanoide magro de madeira, bracos longos com 3 dedos, galhos nas costas e na cabeca. */
public class WoodStalkerModel extends EntityModel<WoodStalker> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart armR;
    private final ModelPart armL;
    private final ModelPart legR;
    private final ModelPart legL;

    public WoodStalkerModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.armR = root.getChild("right_arm");
        this.armL = root.getChild("left_arm");
        this.legR = root.getChild("right_leg");
        this.legL = root.getChild("left_leg");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition p = mesh.getRoot();
        PartDefinition head = p.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3, -7, -3, 6, 7, 6), PartPose.offset(0, 0, 0));
        head.addOrReplaceChild("twig_a", CubeListBuilder.create().texOffs(0, 32).addBox(-0.5F, -5, -0.5F, 1, 5, 1), PartPose.offsetAndRotation(-2, -7, 0, 0, 0, -0.5F));
        head.addOrReplaceChild("twig_b", CubeListBuilder.create().texOffs(0, 32).addBox(-0.5F, -5, -0.5F, 1, 5, 1), PartPose.offsetAndRotation(2, -7, 1, 0.2F, 0, 0.6F));
        head.addOrReplaceChild("twig_c", CubeListBuilder.create().texOffs(0, 32).addBox(-0.5F, -4, -0.5F, 1, 5, 1), PartPose.offsetAndRotation(0, -7, -2, -0.5F, 0, 0.1F));
        PartDefinition body = p.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 14).addBox(-3, 0, -2, 6, 12, 4), PartPose.ZERO);
        body.addOrReplaceChild("knot_r", CubeListBuilder.create().texOffs(12, 32).addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3), PartPose.offset(-4, 1, 0));
        body.addOrReplaceChild("knot_l", CubeListBuilder.create().texOffs(12, 32).addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3), PartPose.offset(4, 1, 0));
        for (int i = 0; i < 3; i++) {
            body.addOrReplaceChild("branch" + i, CubeListBuilder.create().texOffs(0, 32).addBox(-0.5F, -6, -0.5F, 1, 5, 1),
                    PartPose.offsetAndRotation(-2 + i * 2, 3 + i, 2, 0.5F + i * 0.1F, 0, (i - 1) * 0.4F));
        }
        for (int s = -1; s <= 1; s += 2) {
            PartDefinition arm = p.addOrReplaceChild(s < 0 ? "right_arm" : "left_arm",
                    CubeListBuilder.create().texOffs(24, 14).addBox(-1, 0, -1, 2, 16, 2), PartPose.offset(4.5F * s, 1, 0));
            for (int f = -1; f <= 1; f++) {
                arm.addOrReplaceChild("finger" + f, CubeListBuilder.create().texOffs(0, 32).addBox(-0.5F, 0, -0.5F, 1, 5, 1),
                        PartPose.offsetAndRotation(f * 0.7F, 16, 0, 0.15F, 0, f * 0.2F));
            }
            p.addOrReplaceChild(s < 0 ? "right_leg" : "left_leg",
                    CubeListBuilder.create().texOffs(34, 14).addBox(-1.5F, 0, -1.5F, 3, 12, 3), PartPose.offset(1.8F * s, 12, 0));
        }
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(WoodStalker e, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.xRot = headPitch * Mth.DEG_TO_RAD + (e.isCrouched() ? 0.5F : 0.0F);
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        float swing = Mth.cos(limbSwing * 0.6F) * 1.0F * limbSwingAmount;
        this.legR.xRot = swing;
        this.legL.xRot = -swing;
        if (e.isGrabbing()) {
            this.armR.xRot = this.armL.xRot = -1.5F + Mth.sin(ageInTicks * 0.3F) * 0.1F;
            this.armR.zRot = 0.2F;
            this.armL.zRot = -0.2F;
        } else if (e.isClimbing()) {
            this.armR.xRot = -2.7F + Mth.sin(ageInTicks * 0.5F) * 0.3F;
            this.armL.xRot = -2.7F - Mth.sin(ageInTicks * 0.5F) * 0.3F;
            this.armR.zRot = this.armL.zRot = 0.0F;
        } else if (e.isAggressive()) {
            this.armR.xRot = this.armL.xRot = -0.9F;
            this.armR.zRot = 0.1F;
            this.armL.zRot = -0.1F;
        } else {
            this.armR.xRot = -swing * 0.8F;
            this.armL.xRot = swing * 0.8F;
            this.armR.zRot = 0.05F + Mth.cos(ageInTicks * 0.09F) * 0.04F;
            this.armL.zRot = -0.05F - Mth.cos(ageInTicks * 0.09F) * 0.04F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
