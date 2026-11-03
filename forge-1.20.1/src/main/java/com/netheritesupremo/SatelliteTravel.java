package com.netheritesupremo;

import com.netheritesupremo.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Viagem pelo portal do satelite. Ida: dimensao atual -> Plutao (dimensao sistemasolar:plutao), guardando de onde
 * a entidade veio. Volta (portal de Plutao): para o lugar guardado, ou para o Overworld se nao houver.
 */
public final class SatelliteTravel {
    public static final ResourceKey<Level> PLUTO = ResourceKey.create(Registries.DIMENSION,
            new ResourceLocation("sistemasolar", "plutao"));
    private static final String ORIGIN = "netheritesupremo_sat_origin";
    private static final String COOLDOWN = "netheritesupremo_portal_cd"; // o mesmo do Olho do Dragao

    private SatelliteTravel() {
    }

    public static void travel(Entity entity, ServerLevel from, BlockPos pos) {
        if (!(entity instanceof LivingEntity) || entity.isPassenger() || entity.isVehicle()) {
            return;
        }
        long now = from.getGameTime();
        CompoundTag data = entity.getPersistentData();
        if (data.getLong(COOLDOWN) > now) {
            return;
        }
        MinecraftServer server = from.getServer();
        Direction facing = entity.getDirection();
        ServerLevel dest;
        double x;
        double y;
        double z;
        if (from.dimension() == PLUTO) {
            dest = null;
            x = y = z = 0;
            String[] o = data.getString(ORIGIN).split("\\|");
            if (o.length == 4) {
                try {
                    dest = server.getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(o[0])));
                    x = Double.parseDouble(o[1]);
                    y = Double.parseDouble(o[2]);
                    z = Double.parseDouble(o[3]);
                } catch (RuntimeException e) {
                    dest = null;
                }
            }
            if (dest == null) {
                dest = server.overworld();
                BlockPos spawn = dest.getSharedSpawnPos();
                BlockPos spot = DragonEyePortals.findSafe(dest, spawn.getX(), spawn.getZ());
                x = spot.getX() + 0.5D;
                y = spot.getY();
                z = spot.getZ() + 0.5D;
            }
        } else {
            dest = server.getLevel(PLUTO);
            if (dest == null) {
                if (entity instanceof ServerPlayer player) {
                    player.displayClientMessage(Component.translatable("message.netheritesupremo.satellite.no_pluto"), true);
                }
                return;
            }
            data.putString(ORIGIN, from.dimension().location() + "|" + (entity.getX() - facing.getStepX() * 2.0D)
                    + "|" + entity.getY() + "|" + (entity.getZ() - facing.getStepZ() * 2.0D));
            BlockPos spot = DragonEyePortals.findSafe(dest, entity.getBlockX(), entity.getBlockZ());
            buildGateway(dest, spot, facing);
            x = spot.getX() + 0.5D;
            y = spot.getY();
            z = spot.getZ() + 0.5D;
        }
        data.putLong(COOLDOWN, now + 100);
        DragonEyePortals.teleportTo(entity, dest, x, y, z);
        dest.playSound(null, BlockPos.containing(x, y, z), SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.4F, 1.4F);
    }

    /** Plataforma de quartzo com um portal 3x3 emoldurado, 3 blocos a frente do ponto de chegada. */
    private static void buildGateway(ServerLevel level, BlockPos spot, Direction facing) {
        Direction side = facing.getClockWise();
        BlockState quartz = Blocks.SMOOTH_QUARTZ.defaultBlockState();
        BlockState frame = Blocks.WAXED_OXIDIZED_CUT_COPPER.defaultBlockState();
        BlockState portal = ModBlocks.SATELLITE_PORTAL.get().defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        for (int step = -1; step <= 5; step++) {
            for (int w = -3; w <= 3; w++) {
                BlockPos base = spot.relative(facing, step).relative(side, w);
                level.setBlock(base.below(), quartz, 3);
                for (int h = 0; h <= 4; h++) {
                    level.setBlock(base.above(h), air, 3);
                }
            }
        }
        BlockPos back = spot.relative(facing, 3);
        for (int w = -2; w <= 2; w++) {
            for (int h = 0; h <= 3; h++) {
                BlockPos pos = back.relative(side, w).above(h);
                boolean edge = Math.abs(w) == 2 || h == 3;
                level.setBlock(pos, edge ? frame : portal, 3);
            }
        }
    }
}
