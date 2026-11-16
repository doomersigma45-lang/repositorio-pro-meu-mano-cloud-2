package com.netheritesupremo.parasite;

import com.netheritesupremo.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * Cao Parasita (parasita novo): lobo com dentes e espinhos que corre e pula no alvo e, como um creeper, infla e explode.
 * Ao explodir solta 3 vermes, deixa poças de sangue e infecta mobs num raio de 8 blocos.
 */
public class ParasiteHound extends Creeper implements ParasiteMob {
    private boolean burst;

    public ParasiteHound(EntityType<? extends Creeper> type, Level level) {
        super(type, level);
        this.xpReward = 20;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Creeper.createAttributes()
                .add(Attributes.MAX_HEALTH, 45.0D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.4D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SwellGoal(this));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.45F));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        ParasiteBehavior.addTargets(this);
    }

    /** O Creeper chama discard() ao explodir (o metodo da explosao e privado): aproveitamos a remocao. */
    @Override
    public void remove(RemovalReason reason) {
        if (!this.burst && reason == RemovalReason.DISCARDED && this.level() instanceof ServerLevel level
                && this.getSwelling(1.0F) > 0.85F) {
            this.burst = true;
            this.afterBlast(level);
        }
        super.remove(reason);
    }

    private void afterBlast(ServerLevel level) {
        for (Mob m : level.getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(8.0D), e -> !(e instanceof ParasiteMob))) {
            ParasiteBehavior.infect(m);
        }
        for (int i = 0; i < 3; i++) {
            ParasiteWorm w = ModEntities.PARASITE_WORM.get().create(level);
            if (w != null) {
                w.moveTo(this.getX() + level.random.nextDouble() * 2.0D - 1.0D, this.getY() + 0.3D,
                        this.getZ() + level.random.nextDouble() * 2.0D - 1.0D, level.random.nextFloat() * 360.0F, 0.0F);
                level.addFreshEntity(w);
            }
        }
        if (ForgeEventFactory.getMobGriefingEvent(level, this)) {
            BlockPos c = this.blockPosition();
            for (int i = 0; i < 5; i++) {
                BlockPos p = c.offset(level.random.nextInt(5) - 2, level.random.nextInt(3) - 1, level.random.nextInt(5) - 2);
                if (level.getBlockState(p).isAir() && level.getBlockState(p.below()).isFaceSturdy(level, p.below(), Direction.UP)) {
                    level.setBlock(p, ParasiteBlood.BLOCK.get().defaultBlockState(), 3);
                }
            }
        }
    }
}
