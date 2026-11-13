package com.netheritesupremo.client;

import com.netheritesupremo.mob.Automaton;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Automato (64x64): placas metalicas, cabeca de capacete, bracos enormes de 3 dedos, pernas curtas. */
public class AutomatonModel extends EntityModel<Automaton> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart armR;
    private final ModelPart armL;
    private final ModelPart legR;
    private final ModelPart legL;

    public AutomatonModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.armR = root.getChild("arm_right");
        this.armL = root.getChild("arm_left");
        this.legR = root.getChild("leg_right");
        this.legL = root.getChild("leg_left");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition p = mesh.getRoot();
        p.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 14).addBox(-6, -14, -3, 12, 14, 6), PartPose.offset(0, 16, 0));
        p.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3, -6, -3, 6, 6, 6), PartPose.offset(0, 2, 0));
        CubeListBuilder arm = CubeListBuilder.create().texOffs(0, 36).addBox(-2.5F, 0, -2.5F, 5, 16, 5)
                .texOffs(22, 36).addBox(-2.5F, 16, -1.5F, 1.2F, 5, 3)
                .addBox(-0.6F, 16, -1.5F, 1.2F, 5, 3)
                .addBox(1.3F, 16, -1.5F, 1.2F, 5, 3);
        p.addOrReplaceChild("arm_right", arm, PartPose.offset(-8.5F, 5, 0));
        p.addOrReplaceChild("arm_left", arm, PartPose.offset(8.5F, 5, 0));
        CubeListBuilder leg = CubeListBuilder.create().texOffs(36, 36).addBox(-2.5F, 0, -2.5F, 5, 8, 5);
        p.addOrReplaceChild("leg_right", leg, PartPose.offset(-3, 16, 0));
        p.addOrReplaceChild("leg_left", leg, PartPose.offset(3, 16, 0));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(Automaton a, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch) {
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);
        float s = Mth.cos(limbSwing * 0.6662F) * 1.1F * limbSwingAmount;
        this.legR.xRot = s;
        this.legL.xRot = -s;
        if (a.isAggressive()) {
            this.armR.xRot = -1.5F;
            this.armL.xRot = -1.5F;
        } else {
            this.armR.xRot = -s * 0.7F;
            this.armL.xRot = s * 0.7F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
