package com.netheritesupremo.parasite;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.Level;

/**
 * Bombardeiro parasita (Light Bomber do Scape and Run: Parasites): um fantasma voador infectado que, passando sobre o
 * alvo, solta bombas parasitas. Nao queima de dia.
 */
public class ParasiteBomber extends Phantom implements ParasiteMob {
    private int bombCooldown = 60;

    public ParasiteBomber(EntityType<? extends Phantom> type, Level level) {
        super(type, level);
        this.xpReward = 12;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FLYING_SPEED, 0.4D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return true;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            return;
        }
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive() || --this.bombCooldown > 0) {
            return;
        }
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        if (dx * dx + dz * dz < 49.0D && this.getY() > target.getY() + 1.5D) {
            this.bombCooldown = 60;
            ParasiteBomb bomb = new ParasiteBomb(this.level(), this);
            bomb.setPos(this.getX(), this.getY() - 0.4D, this.getZ());
            bomb.shoot(0.0D, -1.0D, 0.0D, 0.35F, 0.0F);
            this.level().addFreshEntity(bomb);
        }
    }
}
