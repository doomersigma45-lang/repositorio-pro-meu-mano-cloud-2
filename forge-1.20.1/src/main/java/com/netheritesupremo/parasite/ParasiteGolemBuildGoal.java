package com.netheritesupremo.parasite;

import com.netheritesupremo.registry.ModEntities;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * Jogadores e endermans parasitas "fabricam" Golems de Ferro Parasitas: montam o T de blocos de ferro com a abobora no
 * topo, um bloco de cada vez, e no fim os blocos somem e o golem nasce (herdando a fase de quem o fez). Se forem
 * interrompidos (alvo, ritual) os blocos colocados sao removidos.
 */
public class ParasiteGolemBuildGoal extends Goal {
    private static final int CAP = 3;
    /** Ordem de montagem: base, meio, bracos e abobora (indices de cells()). */
    private static final int[] CELL_ORDER = {0, 1, 3, 4, 2};
    private final PathfinderMob mob;
    private BlockPos base;
    private boolean xAxis;
    private int step;
    private int timer;
    private boolean done;
    private final List<BlockPos> placed = new ArrayList<>();

    public ParasiteGolemBuildGoal(PathfinderMob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!(this.mob.level() instanceof ServerLevel level) || this.mob.getTarget() != null || ParasiteRituals.isRitualist(this.mob)
                || this.mob.getRandom().nextInt(600) != 0 || !ForgeEventFactory.getMobGriefingEvent(level, this.mob)) {
            return false;
        }
        if (level.getEntitiesOfClass(ParasiteIronGolem.class, this.mob.getBoundingBox().inflate(64.0D)).size() >= CAP) {
            return false;
        }
        return this.findSite(level);
    }

    @Override
    public boolean canContinueToUse() {
        return !this.done && this.mob.getTarget() == null && !ParasiteRituals.isRitualist(this.mob);
    }

    @Override
    public void start() {
        this.step = 0;
        this.timer = 20;
        this.done = false;
        this.placed.clear();
        this.mob.getNavigation().stop();
    }

    @Override
    public void stop() {
        if (!this.done || this.step < 6) {
            this.clearBlocks();
        }
        this.placed.clear();
    }

    private boolean findSite(ServerLevel level) {
        BlockPos o = this.mob.blockPosition();
        for (int i = 0; i < 10; i++) {
            int dx = this.mob.getRandom().nextInt(9) - 4;
            int dz = this.mob.getRandom().nextInt(9) - 4;
            if (Math.abs(dx) < 2 && Math.abs(dz) < 2) {
                continue;
            }
            BlockPos b = o.offset(dx, 0, dz);
            boolean x = this.mob.getRandom().nextBoolean();
            if (!level.getBlockState(b.below()).isFaceSturdy(level, b.below(), Direction.UP)) {
                continue;
            }
            this.base = b;
            this.xAxis = x;
            if (this.cells().stream().allMatch(p -> level.getBlockState(p).isAir())) {
                return true;
            }
        }
        return false;
    }

    private List<BlockPos> cells() {
        Direction side = this.xAxis ? Direction.EAST : Direction.SOUTH;
        return List.of(this.base, this.base.above(), this.base.above(2),
                this.base.above().relative(side), this.base.above().relative(side.getOpposite()));
    }

    private void place(ServerLevel level, BlockPos pos, Block block) {
        level.setBlock(pos, block.defaultBlockState(), 3);
        this.placed.add(pos);
        this.mob.swing(InteractionHand.MAIN_HAND);
        level.playSound(null, pos, block == Blocks.CARVED_PUMPKIN ? SoundEvents.PUMPKIN_CARVE : SoundEvents.ANVIL_PLACE,
                SoundSource.HOSTILE, 0.6F, 0.7F);
    }

    private void clearBlocks() {
        if (!(this.mob.level() instanceof ServerLevel level)) {
            return;
        }
        for (BlockPos p : this.placed) {
            if (level.getBlockState(p).is(Blocks.IRON_BLOCK) || level.getBlockState(p).is(Blocks.CARVED_PUMPKIN)) {
                level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }

    @Override
    public void tick() {
        if (!(this.mob.level() instanceof ServerLevel level) || this.base == null) {
            return;
        }
        this.mob.getLookControl().setLookAt(this.base.getX() + 0.5D, this.base.getY() + 1.5D, this.base.getZ() + 0.5D);
        if (--this.timer > 0) {
            return;
        }
        this.timer = 12;
        List<BlockPos> c = this.cells();
        if (this.step <= 4 && !level.getBlockState(c.get(CELL_ORDER[this.step])).isAir()) {
            this.done = true;
            return;
        }
        switch (this.step) {
            case 0 -> this.place(level, c.get(0), Blocks.IRON_BLOCK);
            case 1 -> this.place(level, c.get(1), Blocks.IRON_BLOCK);
            case 2 -> this.place(level, c.get(3), Blocks.IRON_BLOCK);
            case 3 -> this.place(level, c.get(4), Blocks.IRON_BLOCK);
            case 4 -> this.place(level, c.get(2), Blocks.CARVED_PUMPKIN);
            case 5 -> {
                this.timer = 20;
            }
            default -> {
                this.clearBlocks();
                this.spawnGolem(level);
                this.done = true;
            }
        }
        this.step++;
    }

    private void spawnGolem(ServerLevel level) {
        ParasiteIronGolem g = ModEntities.PARASITE_IRON_GOLEM.get().create(level);
        if (g == null) {
            return;
        }
        g.moveTo(this.base.getX() + 0.5D, this.base.getY(), this.base.getZ() + 0.5D, this.mob.getRandom().nextFloat() * 360.0F, 0.0F);
        if (this.mob.getPersistentData().contains(ParasiteBehavior.SINCE_TAG)) {
            g.getPersistentData().putLong(ParasiteBehavior.SINCE_TAG, this.mob.getPersistentData().getLong(ParasiteBehavior.SINCE_TAG));
        } else {
            ParasiteBehavior.markInfected(g);
        }
        level.addFreshEntity(g);
        level.sendParticles(ParticleTypes.SNEEZE, g.getX(), g.getY() + 1.4D, g.getZ(), 40, 0.8D, 1.2D, 0.8D, 0.05D);
        level.playSound(null, g.getX(), g.getY(), g.getZ(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.HOSTILE, 1.5F, 0.5F);
    }
}
