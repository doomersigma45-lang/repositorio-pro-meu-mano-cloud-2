package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.Entity;

/**
 * Modelo generico de artropodes segmentados do Cambriano (cabeca + segmentos encadeados, lobulos laterais,
 * pernas, bracos de captura, olhos e leque na cauda). Textura 64x64: o corpo usa a area (0,0) e os olhos (0,40).
 */
public class SegmentedModel<T extends Entity> extends EntityModel<T> {
    /** Descricao de uma especie (medidas em pixels de modelo). */
    public record Spec(int segments, float headW, float headH, float headD, float segW, float segH, float segD,
            float taper, float lobeW, int legPairs, boolean arms, int eyes, boolean fan, float wave) {
    }

    public static final Spec ANOMALOCARIS = new Spec(5, 8, 6, 8, 14, 7, 9, 0.55F, 9, 0, true, 2, true, 0.30F);
    public static final Spec TRILOBITE = new Spec(4, 9, 3, 6, 12, 4, 5, 0.35F, 0, 4, false, 2, false, 0.10F);
    public static final Spec OPABINIA = new Spec(6, 5, 4, 5, 7, 5, 6, 0.50F, 4, 0, true, 5, true, 0.28F);

    private final ModelPart root;
    private final ModelPart head;
    private final List<ModelPart> segs = new ArrayList<>();
    private final List<ModelPart> lobesL = new ArrayList<>();
    private final List<ModelPart> lobesR = new ArrayList<>();
    private final float wave;

    public SegmentedModel(ModelPart root, Spec spec) {
        this.root = root;
        this.wave = spec.wave();
        this.head = root.getChild("head");
        ModelPart parent = this.head;
        for (int i = 0; i < spec.segments(); i++) {
            ModelPart seg = parent.getChild("seg" + i);
            segs.add(seg);
            if (spec.lobeW() > 0) {
                lobesL.add(seg.getChild("lobeL" + i));
                lobesR.add(seg.getChild("lobeR" + i));
            }
            parent = seg;
        }
    }

    public static LayerDefinition createLayer(Spec s) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        float length = s.headD() + s.segments() * s.segD();
        float cy = 24.0F - s.segH() / 2.0F - (s.legPairs() > 0 ? 3.0F : 0.0F);
        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-s.headW() / 2, -s.headH() / 2, -s.headD(), s.headW(), s.headH(), s.headD()),
                PartPose.offset(0.0F, cy, -length / 2.0F + s.headD()));
        for (int e = 0; e < s.eyes(); e++) {
            float x = s.eyes() == 1 ? 0 : (e - (s.eyes() - 1) / 2.0F) * (s.eyes() > 2 ? 1.6F : s.headW() * 0.8F);
            float z = s.eyes() > 2 ? -s.headD() * 0.5F - (e % 2) * 1.5F : -s.headD() * 0.4F;
            head.addOrReplaceChild("eye" + e,
                    CubeListBuilder.create().texOffs(0, 40).addBox(-0.5F, -2.0F, -0.5F, 1, 2, 1),
                    PartPose.offset(x, -s.headH() / 2.0F, z));
        }
        if (s.arms()) {
            for (int side = -1; side <= 1; side += 2) {
                head.addOrReplaceChild(side < 0 ? "armL" : "armR",
                        CubeListBuilder.create().texOffs(0, 0).addBox(-1, 0, -7, 2, 2, 7),
                        PartPose.offsetAndRotation(side * (s.headW() / 2.0F - 1.5F), s.headH() / 2.0F - 1.0F, -s.headD() + 1.0F,
                                0.5F, side * 0.15F, 0.0F));
            }
        }
        PartDefinition parent = head;
        float prevD = 0.0F;
        PartDefinition lastSeg = null;
        for (int i = 0; i < s.segments(); i++) {
            float w = Math.max(3.0F, s.segW() * (1.0F - s.taper() * i / s.segments()));
            float h = Math.max(2.0F, s.segH() * (1.0F - s.taper() * 0.5F * i / s.segments()));
            PartDefinition seg = parent.addOrReplaceChild("seg" + i,
                    CubeListBuilder.create().texOffs(0, 0).addBox(-w / 2, -h / 2, 0, w, h, s.segD()),
                    PartPose.offset(0.0F, 0.0F, i == 0 ? 0.0F : prevD));
            if (s.lobeW() > 0) {
                seg.addOrReplaceChild("lobeL" + i,
                        CubeListBuilder.create().texOffs(0, 0).addBox(0, -0.5F, 0, s.lobeW(), 1, s.segD()),
                        PartPose.offset(w / 2.0F, 0.0F, 0.0F));
                seg.addOrReplaceChild("lobeR" + i,
                        CubeListBuilder.create().texOffs(0, 0).addBox(-s.lobeW(), -0.5F, 0, s.lobeW(), 1, s.segD()),
                        PartPose.offset(-w / 2.0F, 0.0F, 0.0F));
            }
            if (i < s.legPairs()) {
                seg.addOrReplaceChild("legL" + i, CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0, -0.5F, 1, 3, 1),
                        PartPose.offsetAndRotation(w / 2.0F, h / 2.0F, s.segD() / 2.0F, 0.0F, 0.0F, -0.6F));
                seg.addOrReplaceChild("legR" + i, CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0, -0.5F, 1, 3, 1),
                        PartPose.offsetAndRotation(-w / 2.0F, h / 2.0F, s.segD() / 2.0F, 0.0F, 0.0F, 0.6F));
            }
            parent = seg;
            prevD = s.segD();
            lastSeg = seg;
        }
        if (s.fan() && lastSeg != null) {
            for (int p = -1; p <= 1; p++) {
                lastSeg.addOrReplaceChild("fan" + (p + 1),
                        CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.5F, 0, 2, 1, 5),
                        PartPose.offsetAndRotation(0.0F, 0.0F, s.segD(), 0.0F, p * 0.45F, 0.0F));
            }
        }
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch) {
        float amp = wave * (0.55F + Math.min(1.0F, limbSwingAmount * 3.0F) * 0.9F);
        head.yRot = netHeadYaw * ((float) Math.PI / 180F) * 0.3F;
        for (int i = 0; i < segs.size(); i++) {
            segs.get(i).yRot = (float) Math.sin(ageInTicks * 0.25F - i * 0.7F) * amp * 0.5F;
        }
        for (int i = 0; i < lobesL.size(); i++) {
            float flap = (float) Math.sin(ageInTicks * 0.35F + i * 0.6F) * 0.35F;
            lobesL.get(i).zRot = flap;
            lobesR.get(i).zRot = -flap;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
