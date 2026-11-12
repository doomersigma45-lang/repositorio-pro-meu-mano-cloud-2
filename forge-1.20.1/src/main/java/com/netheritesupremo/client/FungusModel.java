package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.netheritesupremo.mob.Fungus;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Fungus (64x64): tronco apodrecido, chapeu de cogumelo no lugar da cabeca, 4 raizes e cogumelos nas costas. */
public class FungusModel extends EntityModel<Fungus> {
    private final ModelPart root;
    private final ModelPart[] legs = new ModelPart[4];

    public FungusModel(ModelPart root) {
        this.root = root;
        for (int i = 0; i < 4; i++) {
            this.legs[i] = root.getChild("leg" + i);
        }
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition p = mesh.getRoot();
        p.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4, -8, -7, 8, 8, 14), PartPose.offset(0, 14, 0));
        PartDefinition cap = p.addOrReplaceChild("cap", CubeListBuilder.create().texOffs(0, 24).addBox(-6, -3, -6, 12, 3, 12),
                PartPose.offset(0, 7, -8));
        cap.addOrReplaceChild("cap_top", CubeListBuilder.create().texOffs(0, 40).addBox(-5, -2, -5, 10, 2, 10),
                PartPose.offset(0, -3, 0));
        float[][] shrooms = {{-2, 6}, {2, 2}, {-1, -2}, {2, -5}};
        for (int i = 0; i < shrooms.length; i++) {
            PartDefinition s = p.addOrReplaceChild("shroom" + i, CubeListBuilder.create().texOffs(50, 40).addBox(-0.5F, -3, -0.5F, 1, 3, 1),
                    PartPose.offset(shrooms[i][0], 6, shrooms[i][1]));
            s.addOrReplaceChild("shroom_cap" + i, CubeListBuilder.create().texOffs(0, 54).addBox(-2, -2, -2, 4, 2, 4),
                    PartPose.offset(0, -3, 0));
        }
        CubeListBuilder leg = CubeListBuilder.create().texOffs(46, 24).addBox(-1.5F, 0, -1.5F, 3, 10, 3);
        for (int i = 0; i < 4; i++) {
            p.addOrReplaceChild("leg" + i, leg, PartPose.offset(i % 2 == 0 ? -3 : 3, 14, i < 2 ? -5 : 5));
        }
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(Fungus f, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch) {
        float a = Mth.cos(limbSwing * 0.6662F) * 1.2F * limbSwingAmount;
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
