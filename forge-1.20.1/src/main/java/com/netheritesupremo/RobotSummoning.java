package com.netheritesupremo;

import com.netheritesupremo.entity.Robot;
import com.netheritesupremo.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Invoca o robo como o golem de ferro: bloco de ferro no pe, sinalizador no corpo, blocos de
 * diamante nos dois bracos (T) e uma abobora talhada (ou jack-o-lantern) por ultimo na cabeca.
 */
public class RobotSummoning {
    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof Level level) || level.isClientSide) {
            return;
        }
        BlockState placed = event.getPlacedBlock();
        if (!placed.is(Blocks.CARVED_PUMPKIN) && !placed.is(Blocks.JACK_O_LANTERN)) {
            return;
        }
        BlockPos head = event.getPos();
        BlockPos body = head.below();
        BlockPos feet = body.below();
        if (!level.getBlockState(body).is(Blocks.BEACON) || !level.getBlockState(feet).is(Blocks.IRON_BLOCK)) {
            return;
        }
        for (Direction arm : new Direction[] {Direction.EAST, Direction.SOUTH}) {
            BlockPos left = body.relative(arm);
            BlockPos right = body.relative(arm.getOpposite());
            if (level.getBlockState(left).is(Blocks.DIAMOND_BLOCK) && level.getBlockState(right).is(Blocks.DIAMOND_BLOCK)) {
                summon(level, head, body, feet, left, right);
                return;
            }
        }
    }

    private static void summon(Level level, BlockPos... parts) {
        for (BlockPos pos : parts) {
            BlockState state = level.getBlockState(pos);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
            level.levelEvent(2001, pos, Block.getId(state));
        }
        BlockPos feet = parts[2];
        Robot robot = ModEntities.ROBOT.get().create(level);
        if (robot != null) {
            robot.setPlayerCreated(true);
            robot.moveTo(feet.getX() + 0.5D, feet.getY() + 0.05D, feet.getZ() + 0.5D, 0.0F, 0.0F);
            level.addFreshEntity(robot);
        }
        for (BlockPos pos : parts) {
            level.blockUpdated(pos, Blocks.AIR);
        }
    }
}
