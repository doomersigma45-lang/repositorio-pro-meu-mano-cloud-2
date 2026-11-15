package com.netheritesupremo.parasite;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.registry.ModEntities;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Chuva ocasional de meteoros de sangue parasita no Overworld (mais frequente durante um surto). */
@Mod.EventBusSubscriber(modid = NetheriteSupremo.MODID)
public final class ParasiteMeteorEvents {
    private static final Vec3 VELOCITY = new Vec3(0.5D, -1.4D, 0.35D);

    private ParasiteMeteorEvents() {
    }

    /** Faz um meteoro cair em (x, z) vindo do alto. */
    public static boolean spawn(ServerLevel level, double x, double z) {
        if (!level.hasChunkAt(BlockPos.containing(x, 64, z))) {
            return false;
        }
        double y = level.getHeight(Heightmap.Types.WORLD_SURFACE, (int) Math.floor(x), (int) Math.floor(z));
        double height = Math.min(110.0D, level.getMaxBuildHeight() - 6 - y);
        if (height < 30.0D) {
            return false;
        }
        ParasiteMeteor m = ModEntities.PARASITE_METEOR.get().create(level);
        if (m == null) {
            return false;
        }
        double t = height / -VELOCITY.y;
        m.moveTo(x - VELOCITY.x * t, y + height, z - VELOCITY.z * t, 0.0F, 0.0F);
        m.setDeltaMovement(VELOCITY);
        level.addFreshEntity(m);
        return true;
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.getServer().getTickCount() % 200 != 0) {
            return;
        }
        ServerLevel level = event.getServer().overworld();
        if (level.random.nextInt(ParasiteOutbreak.active ? 50 : 120) != 0) {
            return;
        }
        List<ServerPlayer> players = level.players();
        if (players.isEmpty() || !level.getEntitiesOfClass(ParasiteMeteor.class, players.get(0).getBoundingBox().inflate(300.0D)).isEmpty()) {
            return;
        }
        ServerPlayer p = players.get(level.random.nextInt(players.size()));
        double angle = level.random.nextDouble() * Math.PI * 2.0D;
        double dist = 30.0D + level.random.nextDouble() * 50.0D;
        spawn(level, p.getX() + Math.cos(angle) * dist, p.getZ() + Math.sin(angle) * dist);
    }
}
