/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.HierarchicalModel
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Mob
 */
package com.claudecontent.client.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

public class ColossusModel<T extends Mob>
extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public ColossusModel(ModelPart root) {
        this.root = root;
        this.head = root.m_171324_("head");
        this.rightArm = root.m_171324_("right_arm");
        this.leftArm = root.m_171324_("left_arm");
        this.rightLeg = root.m_171324_("right_leg");
        this.leftLeg = root.m_171324_("left_leg");
    }

    public ModelPart m_142109_() {
        return this.root;
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.f_104204_ = netHeadYaw * ((float)Math.PI / 180);
        this.head.f_104203_ = headPitch * ((float)Math.PI / 180);
        this.rightLeg.f_104203_ = -1.5f * Mth.m_14156_((float)limbSwing, (float)13.0f) * limbSwingAmount;
        this.leftLeg.f_104203_ = 1.5f * Mth.m_14156_((float)limbSwing, (float)13.0f) * limbSwingAmount;
        this.rightLeg.f_104204_ = 0.0f;
        this.leftLeg.f_104204_ = 0.0f;
        if (entity.m_5912_()) {
            float sway = Mth.m_14089_((float)(ageInTicks * 0.15f)) * 0.08f;
            this.rightArm.f_104203_ = -1.6f + sway;
            this.leftArm.f_104203_ = -1.6f - sway;
        } else {
            this.rightArm.f_104203_ = (-0.2f + 1.5f * Mth.m_14156_((float)limbSwing, (float)13.0f)) * limbSwingAmount;
            this.leftArm.f_104203_ = (-0.2f - 1.5f * Mth.m_14156_((float)limbSwing, (float)13.0f)) * limbSwingAmount;
        }
    }
}

