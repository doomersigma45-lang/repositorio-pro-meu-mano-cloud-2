package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.netheritesupremo.mob.AncestralGuardian;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Guardiao Ancestral (128x128): golem de pedra de blocos flutuantes, cabeca colada ao tronco, bracos quase no chao. */
public class AncestralGuardianModel extends EntityModel<AncestralGuardian> {
    private final ModelPart root;
    private final ModelPart torso;
    private final ModelPart head;
    private final ModelPart armR;
    private final ModelPart armL;
    private final ModelPart legR;
    private final ModelPart legL;
    private final ModelPart[] pebbles = new ModelPart[4];

    public AncestralGuardianModel(ModelPart root) {
        this.root = root;
        this.torso = root.getChild("torso");
        this.head = root.getChild("head");
        this.armR = root.getChild("arm_right");
        this.armL = root.getChild("arm_left");
        this.legR = root.getChild("leg_right");
        this.legL = root.getChild("leg_left");
        for (int i = 0; i < 4; i++) {
            this.pebbles[i] = root.getChild("pebble" + i);
        }
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition p = mesh.getRoot();
        p.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 24).addBox(-12, -18, -7, 24, 18, 14), PartPose.offset(0, 7, 0));
        PartDefinition head = p.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-7, -12, -6, 14, 12, 12),
                PartPose.offset(0, -11, 0));
        head.addOrReplaceChild("brow", CubeListBuilder.create().texOffs(54, 0).addBox(-7, -7, -8, 14, 3, 3), PartPose.ZERO);
        for (int s = -1; s <= 1; s += 2) {
            p.addOrReplaceChild(s < 0 ? "arm_right" : "arm_left", CubeListBuilder.create()
                    .texOffs(0, 58).addBox(-5, -5, -5, 10, 10, 10)
                    .texOffs(42, 58).addBox(-4, 6, -4, 8, 14, 8)
                    .texOffs(0, 80).addBox(-5.5F, 21, -5.5F, 11, 10, 11), PartPose.offset(18 * s, -8, 0));
            PartDefinition leg = p.addOrReplaceChild(s < 0 ? "leg_right" : "leg_left",
                    CubeListBuilder.create().texOffs(46, 80).addBox(-4.5F, 0, -4.5F, 9, 6, 9), PartPose.offset(6 * s, 10, 0));
            leg.addOrReplaceChild("shin", CubeListBuilder.create().texOffs(0, 102).addBox(-5.5F, 0, -5.5F, 11, 7, 11),
                    PartPose.offset(0, 7, 0));
        }
        for (int i = 0; i < 4; i++) {
            p.addOrReplaceChild("pebble" + i, CubeListBuilder.create().texOffs(92, 0).addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5), PartPose.ZERO);
        }
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(AncestralGuardian g, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch) {
        boolean dormant = g.isDormant();
        float bob = dormant ? 0.0F : Mth.sin(ageInTicks * 0.08F) * 1.2F;
        this.torso.y = 7 + bob;
        this.head.y = -11 + bob;
        this.armR.y = this.armL.y = -8 + bob;
        for (int i = 0; i < 4; i++) {
            float a = ageInTicks * (dormant ? 0.0F : 0.03F) + i * Mth.HALF_PI;
            this.pebbles[i].x = Mth.cos(a) * 30.0F;
            this.pebbles[i].z = Mth.sin(a) * 30.0F;
            this.pebbles[i].y = -4 + Mth.sin(ageInTicks * 0.07F + i * 1.7F) * 6.0F + (dormant ? 18 : 0);
        }
        if (dormant) {
            this.head.xRot = 0.35F;
            this.head.yRot = 0.0F;
            this.armR.xRot = this.armL.xRot = 0.0F;
            this.legR.xRot = this.legL.xRot = 0.0F;
            return;
        }
        this.head.xRot = headPitch * Mth.DEG_TO_RAD * 0.5F;
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD * 0.5F;
        float swing = Mth.cos(limbSwing * 0.5F) * 0.6F * limbSwingAmount;
        this.legR.xRot = swing;
        this.legL.xRot = -swing;
        if (g.attackAnim > 0.0F) {
            float raise = -2.7F * Mth.sin(g.attackAnim * (float) Math.PI);
            this.armR.xRot = this.armL.xRot = raise;
        } else if (g.isAggressive()) {
            this.armR.xRot = -0.9F + Mth.sin(ageInTicks * 0.15F) * 0.08F;
            this.armL.xRot = -0.9F - Mth.sin(ageInTicks * 0.15F) * 0.08F;
        } else {
            this.armR.xRot = -swing * 0.5F;
            this.armL.xRot = swing * 0.5F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
