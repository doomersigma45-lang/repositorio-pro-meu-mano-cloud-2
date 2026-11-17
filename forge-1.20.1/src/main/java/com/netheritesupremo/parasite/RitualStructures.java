package com.netheritesupremo.parasite;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * Estruturas de ritual que aparecem no mundo a partir da fase 2 do surto: estatua de palha dentro de um cercado com um
 * aldeao e um animal, circulo de velas, totem de ossos e circulo de espantalhos. Sao montadas direto por codigo, longe do
 * jogador, em terreno plano e carregado, e cada uma deixa pelo menos 80 blocos de distancia das outras.
 */
public final class RitualStructures {
    public static final int TYPES = 4;
    private static final int RADIUS = 5;
    private static final int MIN_DISTANCE = 80;

    private RitualStructures() {
    }

    /** Posicoes ja usadas (salvas no mundo) para nao empilhar estruturas. */
    public static class Sites extends SavedData {
        private final List<BlockPos> list = new ArrayList<>();

        public static Sites get(ServerLevel level) {
            return level.getDataStorage().computeIfAbsent(Sites::load, Sites::new, "netheritesupremo_ritual_sites");
        }

        public static Sites load(CompoundTag tag) {
            Sites s = new Sites();
            for (long l : tag.getLongArray("Sites")) {
                s.list.add(BlockPos.of(l));
            }
            return s;
        }

        @Override
        public CompoundTag save(CompoundTag tag) {
            tag.putLongArray("Sites", this.list.stream().mapToLong(BlockPos::asLong).toArray());
            return tag;
        }

        boolean tooClose(BlockPos p) {
            for (BlockPos q : this.list) {
                if (q.distSqr(p) < (double) MIN_DISTANCE * MIN_DISTANCE) {
                    return true;
                }
            }
            return false;
        }
    }

    /** Chamado a cada 10 s durante a fase 2/3 do surto. */
    public static void tick(MinecraftServer server) {
        ServerLevel level = server.overworld();
        for (ServerPlayer player : level.players()) {
            if (level.random.nextInt(12) != 0) {
                continue;
            }
            tryBuild(level, player);
        }
    }

    private static int surface(ServerLevel level, int x, int z) {
        return level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
    }

    private static void tryBuild(ServerLevel level, ServerPlayer player) {
        RandomSource rnd = level.random;
        Sites sites = Sites.get(level);
        for (int attempt = 0; attempt < 8; attempt++) {
            double ang = rnd.nextDouble() * Math.PI * 2.0D;
            double dist = 40.0D + rnd.nextDouble() * 50.0D;
            int x = (int) Math.floor(player.getX() + Math.cos(ang) * dist);
            int z = (int) Math.floor(player.getZ() + Math.sin(ang) * dist);
            BlockPos corner1 = new BlockPos(x - RADIUS - 1, 64, z - RADIUS - 1);
            BlockPos corner2 = new BlockPos(x + RADIUS + 1, 64, z + RADIUS + 1);
            if (!level.hasChunkAt(corner1) || !level.hasChunkAt(corner2) || !level.hasChunkAt(new BlockPos(x - RADIUS - 1, 64, z + RADIUS + 1))
                    || !level.hasChunkAt(new BlockPos(x + RADIUS + 1, 64, z - RADIUS - 1))) {
                continue;
            }
            int y = surface(level, x, z);
            BlockPos origin = new BlockPos(x, y, z);
            if (sites.tooClose(origin) || !flat(level, origin)) {
                continue;
            }
            build(level, rnd.nextInt(TYPES), origin);
            sites.list.add(origin);
            sites.setDirty();
            return;
        }
    }

    /** Terreno firme, seco e quase plano (+-1) nos pontos de amostra do circulo. */
    private static boolean flat(ServerLevel level, BlockPos origin) {
        int[][] samples = {{0, 0}, {RADIUS, 0}, {-RADIUS, 0}, {0, RADIUS}, {0, -RADIUS}, {4, 4}, {-4, 4}, {4, -4}, {-4, -4}};
        for (int[] s : samples) {
            int sx = origin.getX() + s[0];
            int sz = origin.getZ() + s[1];
            int h = surface(level, sx, sz);
            if (Math.abs(h - origin.getY()) > 1) {
                return false;
            }
            BlockPos g = new BlockPos(sx, h - 1, sz);
            BlockState gs = level.getBlockState(g);
            if (!gs.getFluidState().isEmpty() || !level.getFluidState(g.above()).isEmpty() || !gs.isFaceSturdy(level, g, Direction.UP)
                    || gs.is(BlockTags.LEAVES) || gs.is(BlockTags.LOGS) || gs.is(BlockTags.ICE)) {
                return false;
            }
        }
        return true;
    }

    // ------------------------------------------------------------------ montagem

    /** Monta a estrutura {@code type} (0 a 3) com o centro em {@code o} (primeiro bloco livre acima do chao). */
    public static void build(ServerLevel level, int type, BlockPos o) {
        clear(level, o);
        switch (Math.floorMod(type, TYPES)) {
            case 0 -> effigy(level, o);
            case 1 -> candleCircle(level, o);
            case 2 -> boneTotem(level, o);
            default -> scarecrowRing(level, o);
        }
    }

    /** Limpa o volume (arvores, plantas) e deixa o chao firme embaixo. */
    private static void clear(ServerLevel level, BlockPos o) {
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                for (int dy = 0; dy <= 8; dy++) {
                    BlockPos p = o.offset(dx, dy, dz);
                    BlockState s = level.getBlockState(p);
                    if (!s.isAir() && s.getFluidState().isEmpty() && !s.hasBlockEntity() && s.getDestroySpeed(level, p) >= 0.0F) {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
                BlockPos g = o.offset(dx, -1, dz);
                BlockState gs = level.getBlockState(g);
                if (gs.isAir() || gs.canBeReplaced() && gs.getFluidState().isEmpty()) {
                    level.setBlock(g, Blocks.COARSE_DIRT.defaultBlockState(), 2);
                }
            }
        }
    }

    private static void put(ServerLevel level, BlockPos p, BlockState s) {
        level.setBlock(p, s, 3);
    }

    private static void floor(ServerLevel level, BlockPos o, int r2, BlockState a, BlockState b) {
        int r = (int) Math.ceil(Math.sqrt(r2));
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                if (dx * dx + dz * dz <= r2) {
                    BlockPos g = o.offset(dx, -1, dz);
                    if (level.getBlockState(g).getFluidState().isEmpty()) {
                        put(level, g, level.random.nextInt(4) == 0 ? b : a);
                    }
                }
            }
        }
    }

    private static void fix(ServerLevel level, List<BlockPos> positions) {
        for (BlockPos p : positions) {
            level.setBlock(p, Block.updateFromNeighbourShapes(level.getBlockState(p), level, p), 3);
        }
    }

    private static BlockState pumpkin(Direction facing) {
        return Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, facing);
    }

    private static BlockState skull(RandomSource r, boolean wither) {
        return (wither ? Blocks.WITHER_SKELETON_SKULL : Blocks.SKELETON_SKULL).defaultBlockState().setValue(SkullBlock.ROTATION, r.nextInt(16));
    }

    private static BlockState candle(RandomSource r) {
        return Blocks.RED_CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, 1 + r.nextInt(4)).setValue(CandleBlock.LIT, Boolean.TRUE);
    }

    private static void spawn(ServerLevel level, EntityType<? extends Mob> type, BlockPos p) {
        Mob m = type.create(level);
        if (m == null) {
            return;
        }
        m.moveTo(p.getX() + 0.5D, p.getY(), p.getZ() + 0.5D, level.random.nextFloat() * 360.0F, 0.0F);
        m.setPersistenceRequired();
        level.addFreshEntity(m);
    }

    private static EntityType<? extends Mob> animal(RandomSource r) {
        return switch (r.nextInt(3)) {
            case 0 -> EntityType.COW;
            case 1 -> EntityType.PIG;
            default -> EntityType.SHEEP;
        };
    }

    /** Estatua grande de palha (com chifres de osso e cabeca de abobora) dentro de um cercado com um aldeao e um animal. */
    private static void effigy(ServerLevel level, BlockPos o) {
        RandomSource r = level.random;
        floor(level, o, 20, Blocks.COARSE_DIRT.defaultBlockState(), Blocks.NETHER_WART_BLOCK.defaultBlockState());
        List<BlockPos> fences = new ArrayList<>();
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                if (Math.max(Math.abs(dx), Math.abs(dz)) == 4) {
                    for (int dy = 0; dy < 2; dy++) {
                        BlockPos p = o.offset(dx, dy, dz);
                        put(level, p, Blocks.DARK_OAK_FENCE.defaultBlockState());
                        fences.add(p);
                    }
                }
            }
        }
        fix(level, fences);
        for (int sx : new int[] {-4, 4}) {
            for (int sz : new int[] {-4, 4}) {
                put(level, o.offset(sx, 2, sz), Blocks.SOUL_LANTERN.defaultBlockState());
            }
        }
        BlockState hay = Blocks.HAY_BLOCK.defaultBlockState();
        for (int dy = 0; dy <= 1; dy++) {
            put(level, o.offset(-1, dy, 0), hay);
            put(level, o.offset(1, dy, 0), hay);
        }
        for (int dx = -1; dx <= 1; dx++) {
            put(level, o.offset(dx, 2, 0), hay);
        }
        put(level, o.offset(0, 3, 0), hay);
        put(level, o.offset(0, 4, 0), hay);
        for (int dx = -3; dx <= 3; dx++) {
            put(level, o.offset(dx, 4, 0), dx == 0 ? Blocks.RED_WOOL.defaultBlockState() : hay);
        }
        put(level, o.offset(0, 5, 0), pumpkin(Direction.SOUTH));
        put(level, o.offset(-1, 6, 0), Blocks.BONE_BLOCK.defaultBlockState());
        put(level, o.offset(1, 6, 0), Blocks.BONE_BLOCK.defaultBlockState());
        put(level, o.offset(0, 6, 0), hay);
        spawn(level, EntityType.VILLAGER, o.offset(-2, 0, 2));
        spawn(level, animal(r), o.offset(2, 0, -2));
    }

    /** Circulo de velas vermelhas acesas em volta de um altar com cranio de wither esqueleto. */
    private static void candleCircle(ServerLevel level, BlockPos o) {
        RandomSource r = level.random;
        floor(level, o, 30, Blocks.COARSE_DIRT.defaultBlockState(), Blocks.BLACKSTONE.defaultBlockState());
        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                int d2 = dx * dx + dz * dz;
                if (d2 >= 17 && d2 <= 26 && Math.floorMod(dx + dz, 2) == 0) {
                    put(level, o.offset(dx, 0, dz), candle(r));
                } else if (d2 >= 4 && d2 <= 9 && Math.floorMod(dx + dz, 3) == 0) {
                    put(level, o.offset(dx, -1, dz), Blocks.RED_NETHER_BRICKS.defaultBlockState());
                }
            }
        }
        put(level, o.offset(0, -1, 0), Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState());
        put(level, o.offset(0, 0, 0), Blocks.POLISHED_BLACKSTONE_BRICK_SLAB.defaultBlockState());
        put(level, o.offset(0, 1, 0), skull(r, true));
    }

    /** Poste alto de ossos com cranio, cruzeta e cranios de esqueleto em volta, com velas nos tocos. */
    private static void boneTotem(ServerLevel level, BlockPos o) {
        RandomSource r = level.random;
        floor(level, o, 20, Blocks.SOUL_SOIL.defaultBlockState(), Blocks.COARSE_DIRT.defaultBlockState());
        BlockState bone = Blocks.BONE_BLOCK.defaultBlockState();
        for (int dy = 0; dy <= 4; dy++) {
            put(level, o.offset(0, dy, 0), bone);
        }
        for (Direction d : Direction.Plane.HORIZONTAL) {
            put(level, o.offset(d.getStepX(), 3, d.getStepZ()), bone);
        }
        put(level, o.offset(0, 5, 0), skull(r, false));
        for (int k = 0; k < 6; k++) {
            double a = k * Math.PI / 3.0D;
            BlockPos p = o.offset((int) Math.round(Math.cos(a) * 3.0D), 0, (int) Math.round(Math.sin(a) * 3.0D));
            put(level, p, bone);
            if (k % 2 == 0) {
                put(level, p.above(), bone);
                put(level, p.above(2), candle(r));
            } else {
                put(level, p.above(), skull(r, false));
            }
        }
    }

    /** Seis espantalhos (cerca, palha e abobora) virados para uma fogueira de almas, com dois animais no meio. */
    private static void scarecrowRing(ServerLevel level, BlockPos o) {
        RandomSource r = level.random;
        floor(level, o, 20, Blocks.SOUL_SOIL.defaultBlockState(), Blocks.COARSE_DIRT.defaultBlockState());
        List<BlockPos> fences = new ArrayList<>();
        for (int k = 0; k < 6; k++) {
            double a = k * Math.PI / 3.0D + 0.4D;
            int dx = (int) Math.round(Math.cos(a) * 4.0D);
            int dz = (int) Math.round(Math.sin(a) * 4.0D);
            BlockPos p = o.offset(dx, 0, dz);
            for (int dy = 0; dy < 2; dy++) {
                put(level, p.above(dy), Blocks.OAK_FENCE.defaultBlockState());
                fences.add(p.above(dy));
            }
            put(level, p.above(2), Blocks.HAY_BLOCK.defaultBlockState());
            Direction face = Math.abs(dx) > Math.abs(dz) ? (dx > 0 ? Direction.WEST : Direction.EAST) : (dz > 0 ? Direction.NORTH : Direction.SOUTH);
            put(level, p.above(3), pumpkin(face));
            for (Direction d : Direction.Plane.HORIZONTAL) { // bracos de palha
                if (d.getAxis() != face.getAxis()) {
                    put(level, p.above(2).relative(d), Blocks.OAK_FENCE.defaultBlockState());
                    fences.add(p.above(2).relative(d));
                }
            }
        }
        fix(level, fences);
        put(level, o, Blocks.SOUL_CAMPFIRE.defaultBlockState());
        spawn(level, animal(r), o.offset(2, 0, 0));
        spawn(level, animal(r), o.offset(-2, 0, 1));
    }
}
