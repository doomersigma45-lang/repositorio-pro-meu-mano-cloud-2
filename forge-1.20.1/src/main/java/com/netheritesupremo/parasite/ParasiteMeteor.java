package com.netheritesupremo.parasite;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import com.netheritesupremo.registry.ModEntities;
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
        level.explode(this, this.getX(), this.getY(), this.getZ(), 9.0F, false, Level.ExplosionInteraction.NONE);
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        if (ForgeEventFactory.getMobGriefingEvent(level, this)) {
            BlockPos center = c.below();
            for (BlockPos p : BlockPos.betweenClosed(center.offset(-6, -6, -6), center.offset(6, 6, 6))) {
                if (p.distSqr(center) > 36 + level.random.nextInt(5)) {
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
        for (BlockPos p : BlockPos.betweenClosed(c.offset(-5, -7, -5), c.offset(5, 1, 5))) {
            if (placed >= 16) {
                break;
            }
            if (p.distSqr(c.below()) <= 30 && level.getBlockState(p).isAir() && level.getFluidState(p).isEmpty()
                    && level.getBlockState(p.below()).isFaceSturdy(level, p.below(), Direction.UP)) {
                level.setBlock(p.immutable(), ParasiteBlood.BLOCK.get().defaultBlockState(), 3);
                placed++;
            }
        }
        // 1 chance em 3 de plantar o coracao de uma Arvore Parasita no fundo da cratera
        if (level.random.nextInt(this.getPersistentData().getBoolean("Light") ? 8 : 3) == 0 && ForgeEventFactory.getMobGriefingEvent(level, this)) {
            for (int dy = 1; dy >= -8; dy--) {
                BlockPos hp = c.offset(0, dy, 0);
                if (level.getBlockState(hp).isAir() && level.getFluidState(hp).isEmpty()
                        && level.getBlockState(hp.below()).isFaceSturdy(level, hp.below(), Direction.UP)) {
                    level.setBlock(hp, ParasiteTree.HEART.get().defaultBlockState(), 3);
                    break;
                }
            }
        }
        for (Mob m : level.getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(10.0D), e -> !(e instanceof ParasiteMob))) {
            ParasiteBehavior.infect(m);
        }
        this.spawnParasites(level, c);
        this.discard();
    }

    private static net.minecraft.world.entity.EntityType<? extends Mob> pick(net.minecraft.util.RandomSource r, int stage) {
        int n = r.nextInt(stage >= 3 ? 12 : (stage == 2 ? 9 : 4));
        return switch (n) {
            case 0 -> ModEntities.PARASITE_ZOMBIE.get();
            case 1 -> ModEntities.PARASITE_SKELETON.get();
            case 2 -> ModEntities.PARASITE_SPIDER.get();
            case 3 -> ModEntities.PARASITE_CREEPER.get();
            case 4 -> ModEntities.PARASITE_ENDERMAN.get();
            case 5 -> ModEntities.PARASITE_VILLAGER.get();
            case 6 -> ModEntities.PARASITE_WOLF.get();
            case 7 -> ModEntities.PARASITE_CRAWLER.get();
            case 8 -> ModEntities.PARASITE_HOUND.get();
            case 9 -> ModEntities.PARASITE_PLAYER.get();
            case 10 -> ModEntities.PARASITE_BROOD_MOTHER.get();
            default -> ModEntities.PARASITE_ZOMBIE.get();
        };
    }

    /** O impacto libera parasitas (4 na fase 1 do surto, 6 na 2 e 8 na 3) e 3 vermes, ja na fase do surto. */
    private void spawnParasites(ServerLevel level, BlockPos c) {
        int stage = ParasiteOutbreak.active ? ParasiteOutbreak.stage() : 1;
        boolean light = this.getPersistentData().getBoolean("Light");
        int count = light ? 1 : (stage >= 3 ? 8 : (stage == 2 ? 6 : 4));
        int worms = light ? 1 : 3;
        for (int i = 0; i < count + worms; i++) {
            net.minecraft.world.entity.EntityType<? extends Mob> type = i < worms ? ModEntities.PARASITE_WORM.get() : pick(level.random, stage);
            Mob m = type.create(level);
            if (m == null) {
                continue;
            }
            m.moveTo(c.getX() + 0.5D + level.random.nextInt(9) - 4, c.getY() + 1.0D, c.getZ() + 0.5D + level.random.nextInt(9) - 4,
                    level.random.nextFloat() * 360.0F, 0.0F);
            OutbreakEvents.stampStage(m, stage);
            level.addFreshEntity(m);
        }
    }
}
