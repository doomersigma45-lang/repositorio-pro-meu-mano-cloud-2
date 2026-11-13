package com.netheritesupremo.client;

import com.netheritesupremo.mob.AncientTurtle;
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

/** Tartaruga ancestral (128x128): casco enorme com placas osseas, fosseis e plantas, pescoco grosso, bico, 4 patas com garras. */
public class AncientTurtleModel extends EntityModel<AncientTurtle> {
    private final ModelPart root;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart[] legs = new ModelPart[4];

    public AncientTurtleModel(ModelPart root) {
        this.root = root;
        this.neck = root.getChild("neck");
        this.head = this.neck.getChild("head");
        for (int i = 0; i < 4; i++) {
            this.legs[i] = root.getChild("leg" + i);
        }
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition p = mesh.getRoot();
        p.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 0).addBox(-10, -8, -12, 20, 8, 24)
                .texOffs(0, 34).addBox(-7, -11, -9, 14, 3, 18)
                .texOffs(0, 96).addBox(-1, -13, -6, 2, 2, 6)
                .addBox(-5, -13, 1, 2, 2, 6)
                .addBox(3, -13, 2, 2, 2, 6)
                .texOffs(18, 96).addBox(-6, -12, -3, 3, 1, 3)
                .addBox(2, -12, -6, 3, 1, 3)
                .addBox(-2, -12, 6, 3, 1, 3), PartPose.offset(0, 16, 0));
        p.addOrReplaceChild("belly", CubeListBuilder.create().texOffs(0, 57).addBox(-8, 0, -10, 16, 2, 20), PartPose.offset(0, 16, 0));
        PartDefinition neck = p.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(38, 81).addBox(-3, -3, -7, 6, 6, 7),
                PartPose.offset(0, 15, -12));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(66, 81).addBox(-4, -3.5F, -6, 8, 7, 6),
                PartPose.offset(0, 0, -7));
        head.addOrReplaceChild("beak", CubeListBuilder.create().texOffs(96, 81).addBox(-2, 0, -3, 4, 2, 3),
                PartPose.offset(0, 0.5F, -6));
        CubeListBuilder leg = CubeListBuilder.create().texOffs(0, 81).addBox(-2.5F, 0, -2.5F, 5, 6, 5)
                .texOffs(22, 81).addBox(-2.5F, 5, -4, 5, 1, 2);
        float[] z = {-8, 8};
        for (int i = 0; i < 4; i++) {
            p.addOrReplaceChild("leg" + i, leg, PartPose.offset(i % 2 == 0 ? -9 : 9, 18, z[i / 2]));
        }
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(AncientTurtle t, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch) {
        boolean hiding = t.isHiding();
        this.neck.visible = !hiding;
        for (ModelPart leg : this.legs) {
            leg.visible = !hiding;
        }
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F) * 0.6F;
        this.head.xRot = headPitch * ((float) Math.PI / 180F);
        float a = Mth.cos(limbSwing * 0.5F) * 0.7F * limbSwingAmount;
        this.legs[0].xRot = a;
        this.legs[1].xRot = -a;
        this.legs[2].xRot = -a;
        this.legs[3].xRot = a;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
