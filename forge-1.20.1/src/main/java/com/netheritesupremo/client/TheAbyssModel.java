package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.netheritesupremo.mob.TheAbyss;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** O Abismo (128x128): massa escura em gaiola aberta (mostra o nucleo roxo), 6 tentaculos de tamanhos diferentes. */
public class TheAbyssModel extends EntityModel<TheAbyss> {
    /** tamanho relativo de cada tentaculo (o 0 e o maior). */
    private static final float[] SIZE = {1.3F, 1.15F, 1.0F, 0.9F, 0.8F, 0.7F};
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart[] tentacle = new ModelPart[6];
    private final ModelPart[] mid = new ModelPart[6];
    private final ModelPart[] tip = new ModelPart[6];

    public TheAbyssModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        for (int i = 0; i < 6; i++) {
            this.tentacle[i] = root.getChild("tentacle" + i);
            this.mid[i] = this.tentacle[i].getChild("mid");
            this.tip[i] = this.mid[i].getChild("tip");
        }
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition p = mesh.getRoot();
        PartDefinition body = p.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-11, -20, -10, 22, 7, 20)
                .texOffs(0, 28).addBox(-11, -3, -10, 22, 7, 20)
                .texOffs(0, 56).addBox(-12, -18, 5, 24, 20, 8)
                .texOffs(0, 86).addBox(-13, -13, -7, 7, 10, 14)
                .texOffs(0, 86).mirror().addBox(6, -13, -7, 7, 10, 14).mirror(false)
                .texOffs(66, 56).addBox(-5, -13, -5, 10, 10, 10), PartPose.ZERO);
        float[][] spikes = {{-7, -20, -4, 0.0F, 0.3F}, {6, -20, 3, 0.0F, -0.25F}, {0, -20, 6, 0.35F, 0.0F}, {-3, -20, -8, -0.3F, 0.1F}, {8, -20, -6, -0.2F, -0.4F}};
        for (int i = 0; i < spikes.length; i++) {
            body.addOrReplaceChild("spike" + i, CubeListBuilder.create().texOffs(88, 0).addBox(-2, -8, -2, 4, 8, 4),
                    PartPose.offsetAndRotation(spikes[i][0], spikes[i][1], spikes[i][2], spikes[i][3], 0, spikes[i][4]));
        }
        float[][] lumps = {{-12, -16, 2, 0.4F}, {12, 0, -4, -0.5F}, {10, -19, 8, 0.7F}, {-9, 3, 8, -0.3F}};
        for (int i = 0; i < lumps.length; i++) {
            body.addOrReplaceChild("lump" + i, CubeListBuilder.create().texOffs(88, 14).addBox(-3, -3, -3, 6, 6, 6),
                    PartPose.offsetAndRotation(lumps[i][0], lumps[i][1], lumps[i][2], lumps[i][3], lumps[i][3] * 0.7F, 0));
        }
        for (int i = 0; i < 6; i++) {
            double a = Math.toRadians(i * 60 + 15);
            PartDefinition t = p.addOrReplaceChild("tentacle" + i, CubeListBuilder.create().texOffs(66, 78).addBox(-2.5F, 0, -2.5F, 5, 8, 5),
                    PartPose.offset((float) Math.cos(a) * 8, 4, (float) Math.sin(a) * 8));
            PartDefinition m = t.addOrReplaceChild("mid", CubeListBuilder.create().texOffs(88, 78).addBox(-2, 0, -2, 4, 8, 4), PartPose.offset(0, 8, 0));
            m.addOrReplaceChild("tip", CubeListBuilder.create().texOffs(106, 78).addBox(-1.5F, 0, -1.5F, 3, 8, 3), PartPose.offset(0, 8, 0));
        }
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(TheAbyss g, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean dormant = g.isDormant();
        float bob = dormant ? 0.0F : Mth.sin(ageInTicks * 0.07F) * 1.5F;
        this.body.y = bob;
        this.body.yRot = dormant ? 0.0F : Mth.sin(ageInTicks * 0.03F) * 0.12F;
        for (int i = 0; i < 6; i++) {
            ModelPart t = this.tentacle[i];
            double a = Math.toRadians(i * 60 + 15);
            float dx = (float) Math.cos(a);
            float dz = (float) Math.sin(a);
            t.y = 4 + bob;
            float size = SIZE[i];
            if (!dormant && g.isStriking(i)) {
                float rel = g.aimDegrees(i) * Mth.DEG_TO_RAD;
                t.xRot = -1.0F;
                t.yRot = -rel;
                t.zRot = 0.0F;
                t.yScale = size * 2.0F;
                this.mid[i].xRot = 0.0F;
                this.tip[i].xRot = 0.0F;
            } else {
                float w = Mth.sin(ageInTicks * 0.1F + i * 1.3F);
                float lean = dormant ? 0.15F : 0.4F + w * 0.12F;
                t.xRot = lean * dz;
                t.yRot = 0.0F;
                t.zRot = -lean * dx;
                t.yScale = size * (dormant ? 0.5F : 0.8F);
                this.mid[i].xRot = dormant ? 0.0F : Mth.sin(ageInTicks * 0.15F + i) * 0.35F;
                this.tip[i].xRot = dormant ? 0.0F : Mth.sin(ageInTicks * 0.15F + i + 1.0F) * 0.45F;
            }
            t.xScale = t.zScale = size;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
