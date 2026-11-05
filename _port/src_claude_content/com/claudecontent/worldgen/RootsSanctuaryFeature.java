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
    private static final BlockState MOSSY_BRICKS = Blocks.f_50223_.m_49966_();
    private static final BlockState BRICKS = Blocks.f_50222_.m_49966_();
    private static final BlockState CRACKED = Blocks.f_50224_.m_49966_();
    private static final BlockState MOSS = Blocks.f_152543_.m_49966_();

    public RootsSanctuaryFeature() {
        super(NoneFeatureConfiguration.f_67815_);
    }

    public boolean m_142674_(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.m_159774_();
        BlockPos surface = level.m_5452_(Heightmap.Types.WORLD_SURFACE_WG, context.m_159777_());
        if (surface.m_123342_() - level.m_141937_() < 28) {
            return false;
        }
        BlockPos room = new BlockPos(surface.m_123341_(), surface.m_123342_() - 18, surface.m_123343_());
        int shaftX = 7;
        int shaftZ = 0;
        int shaftHeight = level.m_6924_(Heightmap.Types.WORLD_SURFACE_WG, room.m_123341_() + shaftX, room.m_123343_() + shaftZ) - room.m_123342_();
        if (shaftHeight < 4 || shaftHeight > 64) {
            return false;
        }
        for (int x = -6; x <= 6; ++x) {
            for (int z = -6; z <= 6; ++z) {
                int edge = Math.abs(x) == 6 || Math.abs(z) == 6 ? 1 : 0;
                boolean corner = Math.abs(x) == 6 && Math.abs(z) == 6;
                level.m_7731_(room.m_7918_(x, -1, z), (x + z & 3) == 0 ? CRACKED : MOSSY_BRICKS, 3);
                for (int y = 0; y <= 5; ++y) {
                    boolean doorway;
                    boolean bl = doorway = Math.abs(x) == 6 && z == 0 || Math.abs(z) == 6 && x == 0;
                    if (y == 5) {
                        level.m_7731_(room.m_7918_(x, y, z), edge != 0 || corner ? BRICKS : MOSSY_BRICKS, 3);
                        continue;
                    }
                    if (edge != 0 && !doorway) {
                        level.m_7731_(room.m_7918_(x, y, z), corner || y < 4 || (x * 3 + z + y & 3) != 0 ? MOSSY_BRICKS : Blocks.f_50016_.m_49966_(), 3);
                        continue;
                    }
                    level.m_7731_(room.m_7918_(x, y, z), Blocks.f_50016_.m_49966_(), 3);
                }
            }
        }
        for (int x : new int[]{-5, -4, 4, 5}) {
            for (int z : new int[]{-5, -4, 4, 5}) {
                for (int y = 0; y <= 4; ++y) {
                    level.m_7731_(room.m_7918_(x, y, z), MOSSY_BRICKS, 3);
                }
            }
        }
        for (int x = -4; x <= 4; ++x) {
            if (x % 2 != 0) continue;
            level.m_7731_(room.m_7918_(x, 5, -6), BRICKS, 3);
            level.m_7731_(room.m_7918_(x, 5, 6), BRICKS, 3);
        }
        BlockState ladder = (BlockState)Blocks.f_50155_.m_49966_().m_61124_((Property)LadderBlock.f_54337_, (Comparable)Direction.WEST);
        for (int y = 0; y <= shaftHeight; ++y) {
            for (int dx = 0; dx <= 1; ++dx) {
                for (int dz = 0; dz <= 1; ++dz) {
                    level.m_7731_(room.m_7918_(shaftX + dx, y, shaftZ + dz), Blocks.f_50016_.m_49966_(), 3);
                }
            }
            if (y >= shaftHeight) continue;
            level.m_7731_(room.m_7918_(shaftX + 2, y, shaftZ), MOSSY_BRICKS, 3);
            level.m_7731_(room.m_7918_(shaftX + 2, y, shaftZ + 1), MOSSY_BRICKS, 3);
            level.m_7731_(room.m_7918_(shaftX, y, shaftZ - 1), MOSSY_BRICKS, 3);
            level.m_7731_(room.m_7918_(shaftX + 1, y, shaftZ - 1), MOSSY_BRICKS, 3);
            level.m_7731_(room.m_7918_(shaftX, y, shaftZ + 2), MOSSY_BRICKS, 3);
            level.m_7731_(room.m_7918_(shaftX + 1, y, shaftZ + 2), MOSSY_BRICKS, 3);
            if (y <= 0) continue;
            level.m_7731_(room.m_7918_(shaftX + 1, y, shaftZ), ladder, 3);
        }
        for (int dx = 0; dx <= 1; ++dx) {
            for (int dz = 0; dz <= 1; ++dz) {
                level.m_7731_(room.m_7918_(shaftX + dx, -1, shaftZ + dz), MOSSY_BRICKS, 3);
            }
        }
        for (int x = -4; x <= 4; ++x) {
            for (int z = -4; z <= 4; ++z) {
                if ((Math.abs(x) + Math.abs(z)) % 3 != 0) continue;
                level.m_7731_(room.m_7918_(x, 0, z), MOSS, 3);
            }
        }
        level.m_7731_(room, Blocks.f_50225_.m_49966_(), 3);
        level.m_7731_(room.m_7494_(), ((Block)ModBlocks.ROOT_ALTAR.get()).m_49966_(), 3);
        return true;
    }
}

