package com.netheritesupremo.invasion;

import com.netheritesupremo.entity.HybridVillager;
import com.netheritesupremo.entity.PrecursorVillager;
import com.netheritesupremo.registry.ModEntities;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** Um Aldeao Precursor e um aldeao normal adultos lado a lado podem ter um filhote hibrido. */
public class HybridEvents {
    private static final String CD = "netheritesupremo_hybrid_cd";

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.getServer().getTickCount() % 100 != 0) {
            return;
        }
        for (ServerLevel level : event.getServer().getAllLevels()) {
            long now = level.getGameTime();
            for (ServerPlayer p : level.players()) {
                if (level.getEntitiesOfClass(HybridVillager.class, p.getBoundingBox().inflate(48.0D)).size() >= 24) {
                    continue;
                }
                List<PrecursorVillager> parents = level.getEntitiesOfClass(PrecursorVillager.class, p.getBoundingBox().inflate(48.0D),
                        v -> v.getType() == ModEntities.PRECURSOR_VILLAGER.get() && v.isAlive() && !v.isBaby()
                                && v.getPersistentData().getLong(CD) <= now);
                for (PrecursorVillager pv : parents) {
                    List<Villager> mates = level.getEntitiesOfClass(Villager.class, pv.getBoundingBox().inflate(5.0D),
                            v -> v.getType() == net.minecraft.world.entity.EntityType.VILLAGER && v.isAlive() && !v.isBaby() && v.getPersistentData().getLong(CD) <= now);
                    if (mates.isEmpty() || level.random.nextFloat() > 0.5F) {
                        continue;
                    }
                    HybridVillager baby = ModEntities.HYBRID_VILLAGER.get().create(level);
                    if (baby == null) {
                        continue;
                    }
                    baby.moveTo(pv.getX(), pv.getY(), pv.getZ(), level.random.nextFloat() * 360.0F, 0.0F);
                    baby.finalizeSpawn(level, level.getCurrentDifficultyAt(baby.blockPosition()), MobSpawnType.BREEDING, null, null);
                    baby.setAge(-24000);
                    level.addFreshEntity(baby);
                    pv.getPersistentData().putLong(CD, now + 6000);
                    mates.get(0).getPersistentData().putLong(CD, now + 6000);
                    level.sendParticles(ParticleTypes.HEART, pv.getX(), pv.getY() + 1.5D, pv.getZ(), 7, 0.4D, 0.4D, 0.4D, 0.02D);
                }
            }
        }
    }
}
