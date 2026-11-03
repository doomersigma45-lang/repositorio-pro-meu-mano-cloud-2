package com.netheritesupremo;

import com.netheritesupremo.registry.ModBlocks;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

/** Logica dos portais do Olho do Dragao: escolher dimensao (agachado + botao direito) e abrir (botao direito). */
public final class DragonEyePortals {
    private static final String SELECTED = "netheritesupremo_eye_dim";
    private static final String COOLDOWN = "netheritesupremo_portal_cd";
    /** dimensao + posicao do bloco -> dimensao de destino. Os portais duram 30 s, entao nao precisa salvar. */
    public static final Map<String, String> TARGETS = new ConcurrentHashMap<>();

    private DragonEyePortals() {
    }

    /** So quem segura o Olho do Dragao ou a Manopla Suprema pode abrir portais. */
    public static boolean canUse(net.minecraft.world.entity.player.Player player) {
        return DragonEyeEvents.holdsEye(player) || GauntletEvents.holdsGauntlet(player);
    }

    public static String key(ResourceKey<Level> dim, BlockPos pos) {
        return dim.location() + "|" + pos.asLong();
    }

    /** Todas as dimensoes carregadas no servidor (inclui as de outros mods), menos a atual. */
    public static List<ResourceKey<Level>> options(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        List<ResourceKey<Level>> list = new ArrayList<>();
        if (server == null) {
            return list;
        }
        for (ResourceKey<Level> key : server.levelKeys()) {
            if (key != player.level().dimension()) {
                list.add(key);
            }
        }
        list.sort((a, b) -> a.location().toString().compareTo(b.location().toString()));
        return list;
    }

    public static ResourceKey<Level> selected(ServerPlayer player) {
        List<ResourceKey<Level>> list = options(player);
        if (list.isEmpty()) {
            return null;
        }
        String id = player.getPersistentData().getString(SELECTED);
        for (ResourceKey<Level> key : list) {
            if (key.location().toString().equals(id)) {
                return key;
            }
        }
        return list.get(0);
    }

    public static void cycle(ServerPlayer player, int direction) {
        List<ResourceKey<Level>> list = options(player);
        if (list.isEmpty()) {
            return;
        }
        ResourceKey<Level> current = selected(player);
        int index = Math.floorMod(list.indexOf(current) + (direction >= 0 ? 1 : -1), list.size());
        ResourceKey<Level> next = list.get(index);
        player.getPersistentData().putString(SELECTED, next.location().toString());
        player.displayClientMessage(Component.translatable("message.netheritesupremo.dragon_eye.dest",
                next.location().toString()), true);
    }

    public static void open(ServerPlayer player) {
        ResourceKey<Level> target = selected(player);
        if (target == null) {
            return;
        }
        ServerLevel level = player.serverLevel();
        Direction facing = player.getDirection();
        build(level, player.blockPosition().relative(facing, 3), facing, target);
        level.playSound(null, player.blockPosition(), SoundEvents.END_PORTAL_SPAWN, SoundSource.PLAYERS, 0.6F, 1.5F);
        player.displayClientMessage(Component.translatable("message.netheritesupremo.dragon_eye.open",
                target.location().toString()), true);
    }

    /** Portal de 2 de largura por 3 de altura. */
    private static void build(ServerLevel level, BlockPos base, Direction facing, ResourceKey<Level> target) {
        Direction side = facing.getClockWise();
        BlockState portal = ModBlocks.DIMENSION_PORTAL.get().defaultBlockState();
        for (int w = 0; w < 2; w++) {
            for (int h = 0; h < 3; h++) {
                BlockPos pos = base.relative(side, w).above(h);
                BlockState old = level.getBlockState(pos);
                if (old.isAir() || old.canBeReplaced()) {
                    level.setBlock(pos, portal, 3);
                    TARGETS.put(key(level.dimension(), pos), target.location().toString());
                }
            }
        }
    }

    private static boolean passable(ServerLevel level, BlockPos pos) {
        BlockState s = level.getBlockState(pos);
        return s.getCollisionShape(level, pos).isEmpty() && s.getFluidState().isEmpty();
    }

    public static BlockPos findSafe(ServerLevel level, int x, int z) {
        level.getChunk(x >> 4, z >> 4);
        int min = level.getMinBuildHeight();
        int max = min + level.getLogicalHeight() - 3;
        if (!level.dimensionType().hasCeiling()) {
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            if (y > min + 1 && y <= max) {
                return new BlockPos(x, y, z);
            }
        } else {
            for (int y = Math.min(max, min + 120); y > min + 1; y--) {
                BlockPos p = new BlockPos(x, y, z);
                if (!level.getBlockState(p.below()).getCollisionShape(level, p.below()).isEmpty()
                        && level.getBlockState(p.below()).getFluidState().isEmpty()
                        && passable(level, p) && passable(level, p.above())) {
                    return p;
                }
            }
        }
        // dimensao vazia (ou sem espaco): plataforma de obsidiana a meia altura
        BlockPos p = new BlockPos(x, Math.max(min + 2, Math.min(max, min + 64)), z);
        for (int dx = -2; dx <= 5; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                level.setBlock(p.offset(dx, -1, dz), Blocks.OBSIDIAN.defaultBlockState(), 3);
                for (int dy = 0; dy < 4; dy++) {
                    level.setBlock(p.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
        return p;
    }

    /** Teleporta qualquer entidade viva para outra dimensao (jogadores e mobs). */
    public static void teleportTo(Entity entity, ServerLevel dest, double x, double y, double z) {
        float yaw = entity.getYRot();
        float pitch = entity.getXRot();
        if (entity instanceof ServerPlayer player) {
            player.teleportTo(dest, x, y, z, yaw, pitch);
        } else {
            final Vec3 target = new Vec3(x, y, z);
            entity.changeDimension(dest, new ITeleporter() {
                @Override
                public PortalInfo getPortalInfo(Entity e, ServerLevel level, Function<ServerLevel, PortalInfo> defaultInfo) {
                    return new PortalInfo(target, Vec3.ZERO, yaw, pitch);
                }

                @Override
                public boolean playTeleportSound(ServerPlayer player, ServerLevel sourceWorld, ServerLevel destWorld) {
                    return false;
                }
            });
        }
    }

    public static void travel(Entity entity, ServerLevel from, BlockPos pos) {
        if (!(entity instanceof LivingEntity) || entity.isPassenger() || entity.isVehicle()) {
            return;
        }
        String targetId = TARGETS.get(key(from.dimension(), pos));
        long now = from.getGameTime();
        if (targetId == null || entity.getPersistentData().getLong(COOLDOWN) > now) {
            return;
        }
        MinecraftServer server = from.getServer();
        ServerLevel dest = server.getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(targetId)));
        if (dest == null) {
            return;
        }
        entity.getPersistentData().putLong(COOLDOWN, now + 100);
        Direction facing = entity.getDirection();
        BlockPos spot = findSafe(dest, entity.getBlockX(), entity.getBlockZ());
        // portal de volta, 3 blocos a frente do ponto de chegada, com chao por baixo
        BlockPos back = spot.relative(facing, 3);
        Direction side = facing.getClockWise();
        for (int w = 0; w < 2; w++) {
            BlockPos floor = back.relative(side, w).below();
            if (passable(dest, floor)) {
                dest.setBlock(floor, Blocks.OBSIDIAN.defaultBlockState(), 3);
            }
        }
        build(dest, back, facing, from.dimension());
        double x = spot.getX() + 0.5D;
        double z = spot.getZ() + 0.5D;
        float yaw = entity.getYRot();
        float pitch = entity.getXRot();
        if (entity instanceof ServerPlayer player) {
            player.teleportTo(dest, x, spot.getY(), z, yaw, pitch);
        } else {
            final Vec3 target = new Vec3(x, spot.getY(), z);
            entity.changeDimension(dest, new ITeleporter() {
                @Override
                public PortalInfo getPortalInfo(Entity e, ServerLevel level, Function<ServerLevel, PortalInfo> defaultInfo) {
                    return new PortalInfo(target, Vec3.ZERO, yaw, pitch);
                }

                @Override
                public boolean playTeleportSound(ServerPlayer player, ServerLevel sourceWorld, ServerLevel destWorld) {
                    return false;
                }
            });
        }
        dest.playSound(null, spot, SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.4F, 1.4F);
    }
}
