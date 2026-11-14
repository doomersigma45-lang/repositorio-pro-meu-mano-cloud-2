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

/**
 * Wood Stalker e variantes (64x64): humanoide magro de madeira, bracos longos com 3 dedos.
 * Variante 0 = Wood Stalker (galhos); 1 = Ancient Wood (raizes nos bracos, flores e cogumelos nos ombros e costas);
 * 2 = Fungus Stalker (chapeu de cogumelo, cogumelos nas costas, quatro pernas).
 */
public class WoodStalkerModel<T extends WoodStalker> extends EntityModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart armR;
    private final ModelPart armL;
    private final ModelPart legR;
    private final ModelPart legL;
    private final ModelPart legR2;
    private final ModelPart legL2;

    public WoodStalkerModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.armR = root.getChild("right_arm");
        this.armL = root.getChild("left_arm");
        this.legR = root.getChild("right_leg");
        this.legL = root.getChild("left_leg");
        this.legR2 = root.hasChild("right_leg2") ? root.getChild("right_leg2") : null;
        this.legL2 = root.hasChild("left_leg2") ? root.getChild("left_leg2") : null;
    }

    public static LayerDefinition createLayer() {
        return build(0);
    }

    public static LayerDefinition createAncientLayer() {
        return build(1);
    }

    public static LayerDefinition createHybridLayer() {
        return build(2);
    }

    private static void mushroom(PartDefinition parent, String name, float x, float y, float z, float xr, float zr) {
        PartDefinition stem = parent.addOrReplaceChild(name, CubeListBuilder.create().texOffs(50, 40).addBox(-1, -3, -1, 2, 3, 2),
                PartPose.offsetAndRotation(x, y, z, xr, 0, zr));
        stem.addOrReplaceChild(name + "_cap", CubeListBuilder.create().texOffs(26, 56).addBox(-2, -2, -2, 4, 2, 4), PartPose.offset(0, -3, 0));
    }

    private static void flower(PartDefinition parent, String name, float x, float y, float z, float xr, float zr) {
        PartDefinition stem = parent.addOrReplaceChild(name, CubeListBuilder.create().texOffs(58, 56).addBox(-0.5F, -4, -0.5F, 1, 4, 1),
                PartPose.offsetAndRotation(x, y, z, xr, 0, zr));
        stem.addOrReplaceChild(name + "_petals", CubeListBuilder.create().texOffs(44, 56).addBox(-1.5F, -1, -1.5F, 3, 1, 3), PartPose.offset(0, -4, 0));
    }

    private static LayerDefinition build(int variant) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition p = mesh.getRoot();
        PartDefinition head = p.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3, -7, -3, 6, 7, 6), PartPose.ZERO);
        if (variant != 2) {
            head.addOrReplaceChild("twig_a", CubeListBuilder.create().texOffs(0, 32).addBox(-0.5F, -5, -0.5F, 1, 5, 1), PartPose.offsetAndRotation(-2, -7, 0, 0, 0, -0.5F));
            head.addOrReplaceChild("twig_b", CubeListBuilder.create().texOffs(0, 32).addBox(-0.5F, -5, -0.5F, 1, 5, 1), PartPose.offsetAndRotation(2, -7, 1, 0.2F, 0, 0.6F));
            head.addOrReplaceChild("twig_c", CubeListBuilder.create().texOffs(0, 32).addBox(-0.5F, -4, -0.5F, 1, 5, 1), PartPose.offsetAndRotation(0, -7, -2, -0.5F, 0, 0.1F));
        } else {
            head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(0, 40).addBox(-6, -10, -6, 12, 3, 12)
                    .texOffs(0, 56).addBox(-3, -12, -3, 6, 2, 6), PartPose.ZERO);
        }
        PartDefinition body = p.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 14).addBox(-3, 0, -2, 6, 12, 4), PartPose.ZERO);
        body.addOrReplaceChild("knot_r", CubeListBuilder.create().texOffs(12, 32).addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3), PartPose.offset(-4, 1, 0));
        body.addOrReplaceChild("knot_l", CubeListBuilder.create().texOffs(12, 32).addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3), PartPose.offset(4, 1, 0));
        if (variant == 0) {
            for (int i = 0; i < 3; i++) {
                body.addOrReplaceChild("branch" + i, CubeListBuilder.create().texOffs(0, 32).addBox(-0.5F, -6, -0.5F, 1, 5, 1),
                        PartPose.offsetAndRotation(-2 + i * 2, 3 + i, 2, 0.5F + i * 0.1F, 0, (i - 1) * 0.4F));
            }
        } else {
            mushroom(body, "back_m0", -1.8F, 3, 2, -0.6F, -0.2F);
            mushroom(body, "back_m1", 1.6F, 6, 2, -0.7F, 0.25F);
            mushroom(body, "back_m2", -0.5F, 9.5F, 2, -0.5F, 0.0F);
            mushroom(body, "back_m3", 2.0F, 1.5F, 2, -0.4F, 0.3F);
        }
        if (variant == 1) {
            mushroom(body, "sh_m0", -4, -0.5F, 0.5F, 0, -0.2F);
            mushroom(body, "sh_m1", 4.5F, -0.5F, -0.5F, 0, 0.3F);
            flower(body, "fl0", -3.2F, -0.5F, -1.2F, 0.2F, 0.1F);
            flower(body, "fl1", -4.8F, -0.5F, 1.3F, -0.1F, -0.4F);
            flower(body, "fl2", 3.4F, -0.5F, 1.4F, -0.2F, 0.1F);
            flower(body, "fl3", 5F, -1F, -0.5F, 0.1F, 0.4F);
            flower(body, "fl4", 0, -0.5F, 2.3F, -0.5F, 0.0F);
        }
        for (int s = -1; s <= 1; s += 2) {
            PartDefinition arm = p.addOrReplaceChild(s < 0 ? "right_arm" : "left_arm",
                    CubeListBuilder.create().texOffs(24, 14).addBox(-1, 0, -1, 2, 16, 2), PartPose.offset(4.5F * s, 1, 0));
            for (int f = -1; f <= 1; f++) {
                arm.addOrReplaceChild("finger" + f, CubeListBuilder.create().texOffs(0, 32).addBox(-0.5F, 0, -0.5F, 1, 5, 1),
                        PartPose.offsetAndRotation(f * 0.7F, 16, 0, 0.15F, 0, f * 0.2F));
            }
            if (variant == 1) {   // raizes enroladas nos bracos
                CubeListBuilder root = CubeListBuilder.create().texOffs(50, 46).addBox(-1, 0, -1, 2, 7, 2);
                arm.addOrReplaceChild("root_a", root, PartPose.offsetAndRotation(1.6F * s, 1, 0.5F, 0, 0, 0.25F * s));
                arm.addOrReplaceChild("root_b", root, PartPose.offsetAndRotation(-1.4F * s, 5, -0.5F, 0.2F, 0, -0.3F * s));
                arm.addOrReplaceChild("root_c", root, PartPose.offsetAndRotation(0.2F, 9, 1.4F, 0.35F, 0, 0.1F * s));
                arm.addOrReplaceChild("root_d", root, PartPose.offsetAndRotation(0.4F, 12, -1.4F, -0.3F, 0, 0.2F * s));
            }
            if (variant == 2) {
                p.addOrReplaceChild(s < 0 ? "right_leg2" : "left_leg2",
                        CubeListBuilder.create().texOffs(34, 14).addBox(-1.5F, 0, -1.5F, 3, 12, 3), PartPose.offset(1.8F * s, 12, 2.4F));
                p.addOrReplaceChild(s < 0 ? "right_leg" : "left_leg",
                        CubeListBuilder.create().texOffs(34, 14).addBox(-1.5F, 0, -1.5F, 3, 12, 3), PartPose.offset(1.8F * s, 12, -1.2F));
            } else {
                p.addOrReplaceChild(s < 0 ? "right_leg" : "left_leg",
                        CubeListBuilder.create().texOffs(34, 14).addBox(-1.5F, 0, -1.5F, 3, 12, 3), PartPose.offset(1.8F * s, 12, 0));
            }
        }
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(T e, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.xRot = headPitch * Mth.DEG_TO_RAD + (e.isCrouched() ? 0.5F : 0.0F);
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        float swing = Mth.cos(limbSwing * 0.6F) * 1.0F * limbSwingAmount;
        this.legR.xRot = swing;
        this.legL.xRot = -swing;
        if (this.legR2 != null) {
            this.legR2.xRot = -swing;
            this.legL2.xRot = swing;
        }
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
