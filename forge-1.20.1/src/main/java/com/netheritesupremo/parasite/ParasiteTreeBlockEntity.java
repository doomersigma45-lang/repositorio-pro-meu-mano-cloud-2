package com.netheritesupremo.parasite;

import com.netheritesupremo.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;

/**
 * Logica da Arvore Parasita. Fases (pelo numero de blocos parasitados): 1 (&lt;500, 2 blocos de altura), 2 (500+, 5 de
 * altura, comeca a gerar parasitas) e 3 (2500+, 9+ de altura, gera parasitas fortes). Transforma agua em sangue parasita,
 * terra/areia em terra parasita e arvores em tronco/folhas parasitas, num raio que cresce com o consumo.
 */
public class ParasiteTreeBlockEntity extends BlockEntity {
    public static final int PHASE2_AT = 500;
    public static final int PHASE3_AT = 2500;
    private static final int MAX_RADIUS = 72;

    private int consumed;
    private int age;

    public ParasiteTreeBlockEntity(BlockPos pos, BlockState state) {
        super(ParasiteTree.TREE_BE.get(), pos, state);
    }

    public int getConsumed() {
        return this.consumed;
    }

    public int phase() {
        return this.consumed >= PHASE3_AT ? 3 : (this.consumed >= PHASE2_AT ? 2 : 1);
    }

    public int radius() {
        return Math.min(MAX_RADIUS, 8 + this.consumed / 35);
    }

    /** Altura total em blocos (coracao + troncos): fase 1 = 2, fase 2 = 5, fase 3 = 9 a 15. */
    public int height() {
        return switch (this.phase()) {
            case 1 -> 2;
            case 2 -> 5;
            default -> 9 + Math.min(6, (this.consumed - PHASE3_AT) / 400);
        };
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Consumed", this.consumed);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.consumed = tag.getInt("Consumed");
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ParasiteTreeBlockEntity be) {
        if (!(level instanceof ServerLevel sl)) {
            return;
        }
        be.age++;
        int phase = be.phase();
        if (be.age % 20 == 0) {
            be.spread(sl, phase == 3 ? 16 : (phase == 2 ? 8 : 3));
        }
        if (be.age % 40 == 5) {
            be.growStructure(sl);
        }
        if (phase >= 2 && be.age % (phase == 3 ? 60 : 120) == 17) {
            be.spawnParasites(sl, phase);
        }
    }

    // ---------------------------------------------------------------- contaminacao

    private void spread(ServerLevel level, int columns) {
        int r = this.radius();
        RandomSource rnd = level.random;
        boolean changed = false;
        for (int i = 0; i < columns; i++) {
            double ang = rnd.nextDouble() * Math.PI * 2.0D;
            double dist = Math.sqrt(rnd.nextDouble()) * r;
            int x = this.worldPosition.getX() + (int) Math.round(Math.cos(ang) * dist);
            int z = this.worldPosition.getZ() + (int) Math.round(Math.sin(ang) * dist);
            int n = this.convertColumn(level, x, z);
            if (n > 0) {
                this.consumed += n;
                changed = true;
            }
        }
        if (changed) {
            this.setChanged();
        }
    }

    private static boolean isOwn(BlockState s) {
        return s.is(ParasiteTree.HEART.get()) || s.is(ParasiteTree.LOG.get()) || s.is(ParasiteTree.LEAVES.get());
    }

    private static boolean isSoil(BlockState s) {
        return s.is(BlockTags.DIRT) || s.is(BlockTags.SAND) || s.is(Blocks.GRAVEL) || s.is(Blocks.CLAY)
                || s.is(Blocks.FARMLAND) || s.is(Blocks.DIRT_PATH);
    }

    private static boolean isLoosePlant(BlockState s) {
        return s.getBlock() instanceof BushBlock || s.is(Blocks.SNOW) || s.is(Blocks.VINE);
    }

    /** Converte uma coluna (x, z); devolve quantos blocos contaveis foram parasitados. */
    private int convertColumn(ServerLevel level, int x, int z) {
        if (!level.hasChunk(x >> 4, z >> 4)) {
            return 0;
        }
        int top = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
        int minY = Math.max(level.getMinBuildHeight(), top - 80);
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();
        int solid = 0;
        int count = 0;
        for (int y = top - 1; y >= minY && solid < 5; y--) {
            p.set(x, y, z);
            BlockState s = level.getBlockState(p);
            if (s.isAir()) {
                continue;
            }
            FluidState f = s.getFluidState();
            if (f.is(FluidTags.WATER)) {
                boolean plant = s.is(Blocks.KELP) || s.is(Blocks.KELP_PLANT) || isLoosePlant(s);
                if (f.isSource() && (s.getBlock() instanceof LiquidBlock || plant)) {
                    level.setBlock(p, ParasiteBlood.BLOCK.get().defaultBlockState(), 3);
                    count++;
                }
                continue;
            }
            if (!f.isEmpty()) {
                solid++; // lava e outros liquidos: nao mexe
                continue;
            }
            if (s.is(ParasiteTree.DIRT.get())) {
                solid++;
            } else if (isOwn(s)) {
                continue;
            } else if (s.is(BlockTags.LEAVES)) {
                level.setBlock(p, ParasiteTree.LEAVES.get().defaultBlockState(), 3);
                count++;
            } else if (s.is(BlockTags.LOGS)) {
                level.setBlock(p, ParasiteTree.LOG.get().defaultBlockState(), 3);
                count++;
            } else if (isLoosePlant(s)) {
                level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
            } else if (isSoil(s)) {
                level.setBlock(p, ParasiteTree.DIRT.get().defaultBlockState(), 3);
                count++;
                solid++;
            } else if (s.hasBlockEntity()) {
                break;
            } else {
                solid++;
            }
        }
        return count;
    }

    // ---------------------------------------------------------------- estrutura

    private static boolean canGrowInto(LevelReader level, BlockPos pos) {
        BlockState s = level.getBlockState(pos);
        return s.isAir() || (s.canBeReplaced() && s.getFluidState().isEmpty()) || isLoosePlant(s);
    }

    private void setLeaf(ServerLevel level, BlockPos pos) {
        if (canGrowInto(level, pos) && level.getFluidState(pos).isEmpty() && level.isLoaded(pos)) {
            level.setBlock(pos, ParasiteTree.LEAVES.get().defaultBlockState(), 3);
        }
    }

    private void disc(ServerLevel level, BlockPos center, int r2) {
        int r = (int) Math.ceil(Math.sqrt(r2));
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                if (dx * dx + dz * dz <= r2) {
                    this.setLeaf(level, center.offset(dx, 0, dz));
                }
            }
        }
    }

    private void growStructure(ServerLevel level) {
        int h = this.height();
        BlockPos base = this.worldPosition;
        int logs = 0;
        for (int i = 1; i < h; i++) {
            BlockPos p = base.above(i);
            if (!level.isLoaded(p)) {
                return;
            }
            BlockState s = level.getBlockState(p);
            if (s.is(ParasiteTree.LOG.get())) {
                logs++;
                continue;
            }
            if (canGrowInto(level, p) || s.is(BlockTags.LEAVES) || s.is(ParasiteTree.LEAVES.get())) {
                level.setBlock(p, ParasiteTree.LOG.get().defaultBlockState(), 3);
                logs++;
            } else {
                break;
            }
        }
        if (logs < h - 1) {
            return; // tronco bloqueado: sem copa ainda
        }
        BlockPos top = base.above(h - 1);
        switch (this.phase()) {
            case 1 -> {
                for (net.minecraft.core.Direction d : net.minecraft.core.Direction.Plane.HORIZONTAL) {
                    this.setLeaf(level, top.relative(d));
                }
                this.setLeaf(level, top.above());
            }
            case 2 -> {
                this.disc(level, top.below(), 5);
                this.disc(level, top, 5);
                this.disc(level, top.above(), 2);
                this.setLeaf(level, top.above(2));
            }
            default -> {
                this.disc(level, top.below(2), 8);
                this.disc(level, top.below(), 10);
                this.disc(level, top, 8);
                this.disc(level, top.above(), 5);
                this.disc(level, top.above(2), 2);
                this.setLeaf(level, top.above(3));
                // galhos horizontais
                BlockPos by = top.below(3);
                for (net.minecraft.core.Direction d : net.minecraft.core.Direction.Plane.HORIZONTAL) {
                    for (int k = 1; k <= 3; k++) {
                        BlockPos p = by.relative(d, k);
                        if (canGrowInto(level, p) || level.getBlockState(p).is(ParasiteTree.LEAVES.get())) {
                            level.setBlock(p, ParasiteTree.LOG.get().defaultBlockState(), 3);
                        }
                    }
                    this.disc(level, by.relative(d, 3).above(), 2);
                }
            }
        }
    }

    // ---------------------------------------------------------------- parasitas

    private static EntityType<? extends Mob> pickPhase2(RandomSource r) {
        return switch (r.nextInt(12)) {
            case 0 -> ModEntities.PARASITE_ZOMBIE.get();
            case 1 -> ModEntities.PARASITE_SKELETON.get();
            case 2 -> ModEntities.PARASITE_SPIDER.get();
            case 3 -> ModEntities.PARASITE_CREEPER.get();
            case 4 -> ModEntities.PARASITE_VILLAGER.get();
            case 5 -> ModEntities.PARASITE_WOLF.get();
            case 6, 7 -> ModEntities.PARASITE_CRAWLER.get();
            case 8, 9 -> ModEntities.PARASITE_HOUND.get();
            case 10 -> ModEntities.PARASITE_BLOOM.get();
            default -> ModEntities.PARASITE_CHIMERA.get();
        };
    }

    private static EntityType<? extends Mob> pickPhase3(RandomSource r) {
        if (r.nextInt(20) == 0) {
            return ModEntities.PARASITE_WITHER.get();
        }
        return switch (r.nextInt(16)) {
            case 0, 1 -> ModEntities.PARASITE_WARDEN.get();
            case 2, 3 -> ModEntities.PARASITE_PLAYER.get();
            case 4 -> ModEntities.PARASITE_ENDERMAN.get();
            case 5 -> ModEntities.PARASITE_HUNTER.get();
            case 6 -> ModEntities.PARASITE_IRON_GOLEM.get();
            case 7 -> ModEntities.PARASITE_CRAWLER.get();
            case 8 -> ModEntities.PARASITE_HOUND.get();
            case 9 -> ModEntities.PARASITE_BROOD_MOTHER.get();
            case 10 -> ModEntities.PARASITE_STRONGEST_ZOMBIE.get();
            case 11 -> ModEntities.PARASITE_TOTEM.get();
            case 12 -> ModEntities.PARASITE_BLOOM.get();
            case 13 -> ModEntities.PARASITE_ABOMINATION.get();
            case 14 -> ModEntities.PARASITE_AMALGAM.get();
            default -> ModEntities.PARASITE_CHIMERA.get();
        };
    }

    private void spawnParasites(ServerLevel level, int phase) {
        BlockPos c = this.worldPosition;
        if (level.getNearestPlayer(c.getX() + 0.5D, c.getY(), c.getZ() + 0.5D, 80.0D, false) == null) {
            return;
        }
        int r = this.radius();
        int cap = phase == 3 ? 12 : 6;
        AABB box = new AABB(c).inflate(r + 16);
        if (level.getEntitiesOfClass(Mob.class, box, e -> e instanceof ParasiteMob).size() >= cap) {
            return;
        }
        RandomSource rnd = level.random;
        int maxDist = Math.max(10, Math.min(r, 48));
        for (int attempt = 0; attempt < 10; attempt++) {
            double ang = rnd.nextDouble() * Math.PI * 2.0D;
            double dist = 8.0D + rnd.nextDouble() * (maxDist - 8);
            int x = c.getX() + (int) Math.round(Math.cos(ang) * dist);
            int z = c.getZ() + (int) Math.round(Math.sin(ang) * dist);
            if (!level.hasChunk(x >> 4, z >> 4)) {
                continue;
            }
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos feet = new BlockPos(x, y, z);
            if (!level.getBlockState(feet.below()).is(ParasiteTree.DIRT.get()) || !level.getBlockState(feet).isAir()
                    || !level.getBlockState(feet.above()).isAir()) {
                continue;
            }
            Mob m = (phase == 3 ? pickPhase3(rnd) : pickPhase2(rnd)).create(level);
            if (m == null) {
                continue;
            }
            m.moveTo(x + 0.5D, y, z + 0.5D, rnd.nextFloat() * 360.0F, 0.0F);
            if (!level.noCollision(m)) {
                m.discard();
                continue;
            }
            OutbreakEvents.stampStage(m, phase == 3 ? 3 : 2);
            level.addFreshEntity(m);
            return;
        }
    }
}
