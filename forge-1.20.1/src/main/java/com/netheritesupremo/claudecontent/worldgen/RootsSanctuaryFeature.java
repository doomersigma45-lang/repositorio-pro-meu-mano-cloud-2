package com.netheritesupremo.claudecontent.worldgen;

import com.netheritesupremo.claudecontent.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Buried 13x13 chamber with a long stair passage to the forest floor. */
public class RootsSanctuaryFeature extends Feature<NoneFeatureConfiguration> {
    private static final BlockState MOSSY_BRICKS = Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
    private static final BlockState BRICKS = Blocks.STONE_BRICKS.defaultBlockState();
    private static final BlockState CRACKED = Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
    private static final BlockState MOSS = Blocks.MOSS_CARPET.defaultBlockState();

    public RootsSanctuaryFeature() { super(NoneFeatureConfiguration.CODEC); }

    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos surface = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, context.origin());
        if (surface.getY() - level.getMinBuildHeight() < 28) return false;
        BlockPos room = new BlockPos(surface.getX(), surface.getY() - 18, surface.getZ());
        int shaftX = 7;
        int shaftZ = 0;
        int shaftHeight = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG,
                room.getX() + shaftX, room.getZ() + shaftZ) - room.getY();
        if (shaftHeight < 4 || shaftHeight > 64) return false;

        // Buried stone chamber, with roof, floor, open interior and four supports.
        for (int x = -6; x <= 6; x++) for (int z = -6; z <= 6; z++) {
            boolean edge = Math.abs(x) == 6 || Math.abs(z) == 6;
            boolean corner = Math.abs(x) == 6 && Math.abs(z) == 6;
            level.setBlock(room.offset(x, -1, z), ((x + z) & 3) == 0 ? CRACKED : MOSSY_BRICKS, 3);
            for (int y = 0; y <= 5; y++) {
                boolean doorway = (Math.abs(x) == 6 && z == 0) || (Math.abs(z) == 6 && x == 0);
                if (y == 5) {
                    level.setBlock(room.offset(x, y, z), edge || corner ? BRICKS : MOSSY_BRICKS, 3);
                } else if (edge && !doorway) {
                    level.setBlock(room.offset(x, y, z), corner || y < 4 || ((x * 3 + z + y) & 3) != 0
                            ? MOSSY_BRICKS : Blocks.AIR.defaultBlockState(), 3);
                } else {
                    level.setBlock(room.offset(x, y, z), Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
        // Pillars and broken ceiling ribs make the room readable as an old ruin.
        for (int x : new int[]{-5, -4, 4, 5}) for (int z : new int[]{-5, -4, 4, 5}) {
            for (int y = 0; y <= 4; y++) level.setBlock(room.offset(x, y, z), MOSSY_BRICKS, 3);
        }
        for (int x = -4; x <= 4; x++) if (x % 2 == 0) {
            level.setBlock(room.offset(x, 5, -6), BRICKS, 3);
            level.setBlock(room.offset(x, 5, 6), BRICKS, 3);
        }

        // A compact ladder shaft connects the chamber to daylight without spanning chunks.
        BlockState ladder = Blocks.LADDER.defaultBlockState().setValue(net.minecraft.world.level.block.LadderBlock.FACING, Direction.WEST);
        for (int y = 0; y <= shaftHeight; y++) {
            // 2x2 open shaft; masonry on three sides and a supported ladder on the east wall.
            for (int dx = 0; dx <= 1; dx++) for (int dz = 0; dz <= 1; dz++)
                level.setBlock(room.offset(shaftX + dx, y, shaftZ + dz), Blocks.AIR.defaultBlockState(), 3);
            if (y < shaftHeight) {
                level.setBlock(room.offset(shaftX + 2, y, shaftZ), MOSSY_BRICKS, 3);
                level.setBlock(room.offset(shaftX + 2, y, shaftZ + 1), MOSSY_BRICKS, 3);
                level.setBlock(room.offset(shaftX, y, shaftZ - 1), MOSSY_BRICKS, 3);
                level.setBlock(room.offset(shaftX + 1, y, shaftZ - 1), MOSSY_BRICKS, 3);
                level.setBlock(room.offset(shaftX, y, shaftZ + 2), MOSSY_BRICKS, 3);
                level.setBlock(room.offset(shaftX + 1, y, shaftZ + 2), MOSSY_BRICKS, 3);
                if (y > 0) level.setBlock(room.offset(shaftX + 1, y, shaftZ), ladder, 3);
            }
        }
        for (int dx = 0; dx <= 1; dx++) for (int dz = 0; dz <= 1; dz++)
            level.setBlock(room.offset(shaftX + dx, -1, shaftZ + dz), MOSSY_BRICKS, 3);
        for (int x = -4; x <= 4; x++) for (int z = -4; z <= 4; z++) {
            if ((Math.abs(x) + Math.abs(z)) % 3 == 0)
                level.setBlock(room.offset(x, 0, z), MOSS, 3);
        }
        level.setBlock(room, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 3);
        level.setBlock(room.above(), ModBlocks.ROOT_ALTAR.get().defaultBlockState(), 3);
        return true;
    }
}

