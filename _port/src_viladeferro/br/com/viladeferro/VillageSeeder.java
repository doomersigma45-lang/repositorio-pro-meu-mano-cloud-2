/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.MobSpawnType
 *  net.minecraft.world.entity.npc.Villager
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.ServerLevelAccessor
 *  net.minecraft.world.phys.AABB
 */
package br.com.viladeferro;

import br.com.viladeferro.ChiefVillager;
import br.com.viladeferro.FighterVillager;
import br.com.viladeferro.VilaDeFerro;
import br.com.viladeferro.VillageSeedData;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;

public final class VillageSeeder {
    private VillageSeeder() {
    }

    public static void trySeed(ServerLevel level, ServerPlayer player) {
        boolean hasChief;
        List villagers = level.lambda$getNearbyEntities$1(Villager.class, player.getBoundingBox().inflate(64.0), v -> v.getType() == EntityType.VILLAGER && v.isAlive() && !v.isBaby());
        if (villagers.size() < 3) {
            return;
        }
        Villager anchor = (Villager)villagers.get(0);
        BlockPos center = anchor.blockPosition();
        if (!level.lambda$onBlockStateChange$17(center)) {
            return;
        }
        VillageSeedData data = VillageSeedData.get(level);
        if (data.isSeeded(center)) {
            return;
        }
        data.add(center);
        boolean bl = hasChief = !level.lambda$getNearbyEntities$1(ChiefVillager.class, new AABB(center).inflate(128.0)).isEmpty();
        if (!hasChief) {
            VillageSeeder.spawn(level, (EntityType<? extends FighterVillager>)((EntityType)VilaDeFerro.CHIEF.get()), anchor);
        }
        int fighters = Math.min(4, Math.max(2, villagers.size() / 3));
        for (int i = 0; i < fighters; ++i) {
            Villager near = (Villager)villagers.get(level.random.lambda$static$0(villagers.size()));
            VillageSeeder.spawn(level, (EntityType<? extends FighterVillager>)((EntityType)VilaDeFerro.FIGHTER.get()), near);
        }
    }

    private static void spawn(ServerLevel level, EntityType<? extends FighterVillager> type, Villager near) {
        FighterVillager mob = (FighterVillager)type.lambda$appendCustomNameConfig$2((Level)level);
        if (mob == null) {
            return;
        }
        mob.moveTo(near.getX(), near.getY(), near.getZ(), level.random.nextFloat() * 360.0f, 0.0f);
        mob.access$000((ServerLevelAccessor)level, level.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.STRUCTURE, null, null);
        level.addFreshEntity((Entity)mob);
    }
}

