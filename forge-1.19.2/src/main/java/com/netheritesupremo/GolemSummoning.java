package com.netheritesupremo;

import com.netheritesupremo.entity.NetheriteGolem;
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
 * Invoca o golem de netherite do mesmo jeito que o golem de ferro: 4 blocos de netherite em T
 * (dois bracos, corpo e pe) e uma abobora talhada ou jack-o-lantern por cima, colocada por ultimo.
 */
public class GolemSummoning {
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
        if (!isNetherite(level, body) || !isNetherite(level, feet)) {
            return;
        }

        for (Direction arm : new Direction[] {Direction.EAST, Direction.SOUTH}) {
            BlockPos left = body.relative(arm);
            BlockPos right = body.relative(arm.getOpposite());
            if (isNetherite(level, left) && isNetherite(level, right)) {
                summon(level, head, body, feet, left, right);
                return;
            }
        }
    }

    private static boolean isNetherite(Level level, BlockPos pos) {
        return level.getBlockState(pos).is(Blocks.NETHERITE_BLOCK);
    }

    private static void summon(Level level, BlockPos... parts) {
        for (BlockPos pos : parts) {
            BlockState state = level.getBlockState(pos);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
            level.levelEvent(2001, pos, Block.getId(state));
        }
        BlockPos feet = parts[2];
        NetheriteGolem golem = ModEntities.NETHERITE_GOLEM.get().create(level);
        if (golem != null) {
            golem.setPlayerCreated(true);
            golem.moveTo(feet.getX() + 0.5D, feet.getY() + 0.05D, feet.getZ() + 0.5D, 0.0F, 0.0F);
            level.addFreshEntity(golem);
        }
        for (BlockPos pos : parts) {
            level.blockUpdated(pos, Blocks.AIR);
        }
    }
}
