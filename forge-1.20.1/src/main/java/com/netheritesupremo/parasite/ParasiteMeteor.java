package com.netheritesupremo.parasite;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * Meteoro de sangue parasita: cai do ceu em linha reta (sem desacelerar), abre uma cratera pequena ao bater (se
 * mobGriefing permitir), joga uma onda de explosao sem fogo e deixa fontes do liquido vermelho. Mobs perto do impacto
 * sao infectados.
 */
public class ParasiteMeteor extends Fireball {
    public ParasiteMeteor(EntityType<? extends ParasiteMeteor> type, Level level) {
        super(type, level);
        this.setItem(new ItemStack(Items.NETHER_WART_BLOCK));
    }

    @Override
    protected float getInertia() {
        return 1.0F;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.CRIMSON_SPORE;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel sl && this.tickCount % 2 == 0) {
            sl.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY(), this.getZ(), 2, 0.3D, 0.3D, 0.3D, 0.01D);
            sl.sendParticles(ParticleTypes.CRIMSON_SPORE, this.getX(), this.getY(), this.getZ(), 4, 0.5D, 0.5D, 0.5D, 0.02D);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        BlockPos c = this.blockPosition();
        level.explode(this, this.getX(), this.getY(), this.getZ(), 4.0F, false, Level.ExplosionInteraction.NONE);
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        if (ForgeEventFactory.getMobGriefingEvent(level, this)) {
            BlockPos center = c.below();
            for (BlockPos p : BlockPos.betweenClosed(center.offset(-3, -3, -3), center.offset(3, 3, 3))) {
                if (p.distSqr(center) > 9 + level.random.nextInt(3)) {
                    continue;
                }
                BlockState s = level.getBlockState(p);
                if (!s.isAir() && s.getDestroySpeed(level, p) >= 0.0F && !s.hasBlockEntity()) {
                    level.setBlock(p, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
        // poca de sangue no fundo da cratera (ou no chao, se nada foi escavado)
        int placed = 0;
        for (BlockPos p : BlockPos.betweenClosed(c.offset(-2, -4, -2), c.offset(2, 1, 2))) {
            if (placed >= 7) {
                break;
            }
            if (p.distSqr(c.below()) <= 6 && level.getBlockState(p).isAir() && level.getFluidState(p).isEmpty()
                    && level.getBlockState(p.below()).isFaceSturdy(level, p.below(), Direction.UP)) {
                level.setBlock(p.immutable(), ParasiteBlood.BLOCK.get().defaultBlockState(), 3);
                placed++;
            }
        }
        for (Mob m : level.getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(6.0D), e -> !(e instanceof ParasiteMob))) {
            ParasiteBehavior.infect(m);
        }
        this.discard();
    }
}
