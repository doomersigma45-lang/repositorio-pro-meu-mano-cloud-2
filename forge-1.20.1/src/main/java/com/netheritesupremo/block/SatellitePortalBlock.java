package com.netheritesupremo.block;

import com.netheritesupremo.SatelliteTravel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Portal permanente do satelite: leva a Plutao (e de Plutao de volta para onde a pessoa veio). */
public class SatellitePortalBlock extends Block {
    public SatellitePortalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level instanceof ServerLevel server) {
            SatelliteTravel.travel(entity, server, pos);
        }
    }
}
