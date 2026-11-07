package com.netheritesupremo.blackrealm;

import com.netheritesupremo.NetheriteSupremo;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

/** Terra Negra: dimensao para onde vai quem mata o Steve Vermelho. */
public final class BlackRealm {
    public static final ResourceKey<Level> KEY = ResourceKey.create(Registries.DIMENSION,
            new ResourceLocation(NetheriteSupremo.MODID, "black_realm"));

    private BlackRealm() {
    }

    /** Leva o assassino (jogador ou mob) para a Terra Negra, mantendo as coordenadas x/z. */
    public static boolean banish(@Nullable Entity killer, ServerLevel from) {
        if (killer == null || killer.level().isClientSide || !killer.isAlive() || !killer.canChangeDimensions()) {
            return false;
        }
        if (killer.level().dimension() == KEY) {
            return false;
        }
        ServerLevel dest = from.getServer().getLevel(KEY);
        if (dest == null) {
            return false;
        }
        BlockPos spot = findSafeSpot(dest, killer.getBlockX(), killer.getBlockZ());
        return send(killer, dest, spot);
    }

    /** Volta uma entidade para a superficie do Overworld (usado pelo comando de operador). */
    public static boolean leave(Entity entity) {
        ServerLevel ow = entity.getServer().overworld();
        BlockPos spawn = ow.getSharedSpawnPos();
        int y = ow.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawn.getX(), spawn.getZ());
        return send(entity, ow, new BlockPos(spawn.getX(), y, spawn.getZ()));
    }

    private static boolean send(Entity entity, ServerLevel dest, BlockPos spot) {
        Vec3 target = new Vec3(spot.getX() + 0.5D, spot.getY(), spot.getZ() + 0.5D);
        Entity moved = entity.changeDimension(dest, new ITeleporter() {
            @Override
            public PortalInfo getPortalInfo(Entity e, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
                return new PortalInfo(target, Vec3.ZERO, e.getYRot(), e.getXRot());
            }

            @Override
            public boolean playTeleportSound(net.minecraft.server.level.ServerPlayer player, ServerLevel sourceWorld, ServerLevel destWorld) {
                return false;
            }
        });
        return moved != null;
    }

    /** Procura em espiral um ponto de superficie sem liquido; se nao achar, cria uma plataforma. */
    public static BlockPos findSafeSpot(ServerLevel level, int x, int z) {
        for (int r = 0; r <= 96; r += 4) {
            for (int i = 0; i < (r == 0 ? 1 : 8); i++) {
                double angle = Math.PI * 2.0D * i / 8.0D;
                int px = x + (int) Math.round(Math.cos(angle) * r);
                int pz = z + (int) Math.round(Math.sin(angle) * r);
                level.getChunk(px >> 4, pz >> 4); // gera o chunk
                int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, px, pz);
                BlockPos below = new BlockPos(px, y - 1, pz);
                if (level.getFluidState(below).isEmpty() && !level.getBlockState(below).isAir()
                        && level.getFluidState(new BlockPos(px, y, pz)).isEmpty()) {
                    return new BlockPos(px, y, pz);
                }
            }
        }
        int y = Math.max(level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z), level.getSeaLevel() + 2);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                level.setBlockAndUpdate(new BlockPos(x + dx, y - 1, z + dz), Blocks.BLACKSTONE.defaultBlockState());
            }
        }
        return new BlockPos(x, y, z);
    }
}
