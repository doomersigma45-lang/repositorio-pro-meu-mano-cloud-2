package com.netheritesupremo.parasite;

import java.util.EnumSet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.level.Level;

/** Lula parasita: hostil dentro da agua (nada ate o alvo e morde); fora dela se comporta como a lula comum. */
public class ParasiteSquid extends Squid implements ParasiteMob {
    public ParasiteSquid(EntityType<? extends Squid> type, Level level) {
        super(type, level);
        this.xpReward = 10;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Squid.createAttributes()
                .add(Attributes.MAX_HEALTH, 28.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new HuntGoal(this));
        this.goalSelector.addGoal(1, new WanderGoal(this));
        ParasiteBehavior.addTargets(this);
    }

    /** Nada ate o alvo (so dentro da agua) e morde quando encosta. */
    public static class HuntGoal extends Goal {
        private final Squid squid;
        private int cooldown;

        public HuntGoal(Squid squid) {
            this.squid = squid;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public boolean canUse() {
            LivingEntity t = this.squid.getTarget();
            return t != null && t.isAlive() && this.squid.isInWater();
        }

        @Override
        public void tick() {
            LivingEntity t = this.squid.getTarget();
            if (t == null) {
                return;
            }
            double dx = t.getX() - this.squid.getX();
            double dy = t.getY(0.5D) - this.squid.getY(0.5D);
            double dz = t.getZ() - this.squid.getZ();
            double len = Math.max(0.001D, Math.sqrt(dx * dx + dy * dy + dz * dz));
            float s = 0.32F;
            this.squid.setMovementVector((float) (dx / len * s), (float) (dy / len * s), (float) (dz / len * s));
            if (this.cooldown > 0) {
                this.cooldown--;
            } else if (this.squid.getBoundingBox().inflate(0.4D).intersects(t.getBoundingBox())) {
                this.squid.doHurtTarget(t);
                this.cooldown = 20;
            }
        }
    }

    /** Nada a esmo quando nao ha alvo (igual a lula comum). */
    public static class WanderGoal extends Goal {
        private final Squid squid;

        public WanderGoal(Squid squid) {
            this.squid = squid;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public void tick() {
            if (this.squid.getRandom().nextInt(50) == 0 || !this.squid.isInWater() || !this.squid.hasMovementVector()) {
                float f = this.squid.getRandom().nextFloat() * ((float) Math.PI * 2.0F);
                this.squid.setMovementVector((float) Math.cos(f) * 0.2F, -0.1F + this.squid.getRandom().nextFloat() * 0.2F, (float) Math.sin(f) * 0.2F);
            }
        }
    }
}
