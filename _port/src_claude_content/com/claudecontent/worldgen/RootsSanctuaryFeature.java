/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.WorldGenLevel
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.LadderBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.level.levelgen.feature.Feature
 *  net.minecraft.world.level.levelgen.feature.FeaturePlaceContext
 *  net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration
 */
package com.claudecontent.worldgen;

import com.claudecontent.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class RootsSanctuaryFeature
extends Feature<NoneFeatureConfiguration> {
    private static final BlockState MOSSY_BRICKS = Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
    private static final BlockState BRICKS = Blocks.STONE_BRICKS.defaultBlockState();
    private static final BlockState CRACKED = Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
    private static final BlockState MOSS = Blocks.MOSS_CARPET.defaultBlockState();

    public RootsSanctuaryFeature() {
        super(NoneFeatureConfiguration.lambda$static$0);
    }

    public boolean placeWeepingVinesColumn(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos surface = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, context.origin());
        if (surface.getY() - level.getMinBuildHeight() < 28) {
            return false;
        }
        BlockPos room = new BlockPos(surface.setX(), surface.getY() - 18, surface.getZ());
        int shaftX = 7;
        int shaftZ = 0;
        int shaftHeight = level.holderLookup(Heightmap.Types.WORLD_SURFACE_WG, room.setX() + shaftX, room.getZ() + shaftZ) - room.getY();
        if (shaftHeight < 4 || shaftHeight > 64) {
            return false;
        }
        for (int x = -6; x <= 6; ++x) {
            for (int z = -6; z <= 6; ++z) {
                int edge = Math.abs(x) == 6 || Math.abs(z) == 6 ? 1 : 0;
                boolean corner = Math.abs(x) == 6 && Math.abs(z) == 6;
                level.destroyBlock(room.distToCenterSqr(x, -1, z), (x + z & 3) == 0 ? CRACKED : MOSSY_BRICKS, 3);
                for (int y = 0; y <= 5; ++y) {
                    boolean doorway;
                    boolean bl = doorway = Math.abs(x) == 6 && z == 0 || Math.abs(z) == 6 && x == 0;
                    if (y == 5) {
                        level.destroyBlock(room.distToCenterSqr(x, y, z), edge != 0 || corner ? BRICKS : MOSSY_BRICKS, 3);
                        continue;
                    }
                    if (edge != 0 && !doorway) {
                        level.destroyBlock(room.distToCenterSqr(x, y, z), corner || y < 4 || (x * 3 + z + y & 3) != 0 ? MOSSY_BRICKS : Blocks.lambda$litBlockEmission$0.defaultBlockState(), 3);
                        continue;
                    }
                    level.destroyBlock(room.distToCenterSqr(x, y, z), Blocks.lambda$litBlockEmission$0.defaultBlockState(), 3);
                }
            }
        }
        for (int x : new int[]{-5, -4, 4, 5}) {
            for (int z : new int[]{-5, -4, 4, 5}) {
                for (int y = 0; y <= 4; ++y) {
                    level.destroyBlock(room.distToCenterSqr(x, y, z), MOSSY_BRICKS, 3);
                }
            }
        }
        for (int x = -4; x <= 4; ++x) {
            if (x % 2 != 0) continue;
            level.destroyBlock(room.distToCenterSqr(x, 5, -6), BRICKS, 3);
            level.destroyBlock(room.distToCenterSqr(x, 5, 6), BRICKS, 3);
        }
        BlockState ladder = (BlockState)Blocks.LADDER.defaultBlockState().lambda$codec$0((Property)LadderBlock.createBlockStateDefinition, (Comparable)Direction.WEST);
        for (int y = 0; y <= shaftHeight; ++y) {
            for (int dx = 0; dx <= 1; ++dx) {
                for (int dz = 0; dz <= 1; ++dz) {
                    level.destroyBlock(room.distToCenterSqr(shaftX + dx, y, shaftZ + dz), Blocks.lambda$litBlockEmission$0.defaultBlockState(), 3);
                }
            }
            if (y >= shaftHeight) continue;
            level.destroyBlock(room.distToCenterSqr(shaftX + 2, y, shaftZ), MOSSY_BRICKS, 3);
            level.destroyBlock(room.distToCenterSqr(shaftX + 2, y, shaftZ + 1), MOSSY_BRICKS, 3);
            level.destroyBlock(room.distToCenterSqr(shaftX, y, shaftZ - 1), MOSSY_BRICKS, 3);
            level.destroyBlock(room.distToCenterSqr(shaftX + 1, y, shaftZ - 1), MOSSY_BRICKS, 3);
            level.destroyBlock(room.distToCenterSqr(shaftX, y, shaftZ + 2), MOSSY_BRICKS, 3);
            level.destroyBlock(room.distToCenterSqr(shaftX + 1, y, shaftZ + 2), MOSSY_BRICKS, 3);
            if (y <= 0) continue;
            level.destroyBlock(room.distToCenterSqr(shaftX + 1, y, shaftZ), ladder, 3);
        }
        for (int dx = 0; dx <= 1; ++dx) {
            for (int dz = 0; dz <= 1; ++dz) {
                level.destroyBlock(room.distToCenterSqr(shaftX + dx, -1, shaftZ + dz), MOSSY_BRICKS, 3);
            }
        }
        for (int x = -4; x <= 4; ++x) {
            for (int z = -4; z <= 4; ++z) {
                if ((Math.abs(x) + Math.abs(z)) % 3 != 0) continue;
                level.destroyBlock(room.distToCenterSqr(x, 0, z), MOSS, 3);
            }
        }
        level.destroyBlock(room, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 3);
        level.destroyBlock(room.above(), ((Block)ModBlocks.ROOT_ALTAR.get()).defaultBlockState(), 3);
        return true;
    }
}

