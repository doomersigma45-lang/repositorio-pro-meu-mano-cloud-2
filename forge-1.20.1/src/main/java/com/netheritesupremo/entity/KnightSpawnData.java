package com.netheritesupremo.entity;

import com.netheritesupremo.SupremoConfig;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * Guarda no mundo o grupo de cavaleiros de netherite que nasceu naturalmente (ate 10, todos de uma
 * vez). Enquanto algum deles estiver vivo e selvagem, nenhum outro grupo nasce naturalmente. Eles sao
 * persistentes (nao despawnam). Cavaleiros domesticados saem do registro.
 */
public class KnightSpawnData extends SavedData {
    public static final int GROUP_SIZE = 10;

    private static final String NAME = "netheritesupremo_knight";
    private static final String KEY_KNIGHT = "knight";      // formato antigo (um unico cavaleiro)
    private static final String KEY_KNIGHTS = "knights";
    private static final String KEY_DEFEATED = "defeated";
    private static final String KEY_GROUP_TICK = "groupTick";

    private final Set<UUID> knights = new HashSet<>();
    private boolean defeated;
    private long groupTick = Long.MIN_VALUE;

    public static KnightSpawnData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(KnightSpawnData::load, KnightSpawnData::new, NAME);
    }

    public static KnightSpawnData load(CompoundTag tag) {
        KnightSpawnData data = new KnightSpawnData();
        if (tag.hasUUID(KEY_KNIGHT)) {
            data.knights.add(tag.getUUID(KEY_KNIGHT));
        }
        ListTag list = tag.getList(KEY_KNIGHTS, Tag.TAG_INT_ARRAY);
        for (Tag entry : list) {
            data.knights.add(NbtUtils.loadUUID(entry));
        }
        data.defeated = tag.getBoolean(KEY_DEFEATED);
        data.groupTick = tag.contains(KEY_GROUP_TICK) ? tag.getLong(KEY_GROUP_TICK) : Long.MIN_VALUE;
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (UUID id : knights) {
            list.add(NbtUtils.createUUID(id));
        }
        tag.put(KEY_KNIGHTS, list);
        tag.putBoolean(KEY_DEFEATED, defeated);
        tag.putLong(KEY_GROUP_TICK, groupTick);
        return tag;
    }

    /**
     * Pode nascer outro cavaleiro naturalmente? Sim se nao ha grupo vivo (e a config permite), ou se o
     * grupo esta sendo formado neste mesmo tick e ainda nao chegou a 10.
     */
    public boolean canSpawnNaturally(long gameTime) {
        if (!knights.isEmpty()) {
            return gameTime == groupTick && knights.size() < GROUP_SIZE;
        }
        return !defeated || SupremoConfig.KNIGHT_RESPAWNS_AFTER_DEATH.get();
    }

    public void register(UUID id, long gameTime) {
        knights.add(id);
        groupTick = gameTime;
        setDirty();
    }

    /** O cavaleiro foi domesticado: nao conta mais como parte do grupo selvagem. */
    public void unregister(UUID id) {
        if (knights.remove(id)) {
            setDirty();
        }
    }

    public void onKnightDied(UUID id) {
        if (knights.remove(id)) {
            if (knights.isEmpty()) {
                this.defeated = true;
            }
            setDirty();
        }
    }
}
