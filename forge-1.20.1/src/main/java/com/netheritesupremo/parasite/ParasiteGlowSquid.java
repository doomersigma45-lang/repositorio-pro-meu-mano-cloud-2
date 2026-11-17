package com.netheritesupremo.parasite;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.level.Level;

/** Lula brilhante parasita: igual a Lula Parasita, mas mais forte e continua brilhando. */
public class ParasiteGlowSquid extends GlowSquid implements ParasiteMob {
    public ParasiteGlowSquid(EntityType<? extends GlowSquid> type, Level level) {
        super(type, level);
        this.xpReward = 12;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return GlowSquid.createAttributes()
                .add(Attributes.MAX_HEALTH, 34.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new ParasiteSquid.HuntGoal(this));
        this.goalSelector.addGoal(1, new ParasiteSquid.WanderGoal(this));
        ParasiteBehavior.addTargets(this);
    }
}
