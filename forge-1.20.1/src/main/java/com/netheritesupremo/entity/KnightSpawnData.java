package com.netheritesupremo.entity;

import com.netheritesupremo.SupremoConfig;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * Guarda no mundo qual e o unico cavaleiro de netherite que nasceu naturalmente.
 * Enquanto ele estiver vivo, nenhum outro nasce naturalmente. Ele e persistente (nao despawna),
 * entao so sai de cena quando morre.
 */
public class KnightSpawnData extends SavedData {
    private static final String NAME = "netheritesupremo_knight";
    private static final String KEY_KNIGHT = "knight";
    private static final String KEY_DEFEATED = "defeated";

    private UUID knight;
    private boolean defeated;

    public static KnightSpawnData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(KnightSpawnData::load, KnightSpawnData::new, NAME);
    }

    public static KnightSpawnData load(CompoundTag tag) {
        KnightSpawnData data = new KnightSpawnData();
        if (tag.hasUUID(KEY_KNIGHT)) {
            data.knight = tag.getUUID(KEY_KNIGHT);
        }
        data.defeated = tag.getBoolean(KEY_DEFEATED);
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        if (knight != null) {
            tag.putUUID(KEY_KNIGHT, knight);
        }
        tag.putBoolean(KEY_DEFEATED, defeated);
        return tag;
    }

    public boolean canSpawnNaturally() {
        if (knight != null) {
            return false;
        }
        return !defeated || SupremoConfig.KNIGHT_RESPAWNS_AFTER_DEATH.get();
    }

    public void register(UUID id) {
        this.knight = id;
        setDirty();
    }

    public void onKnightDied(UUID id) {
        if (id.equals(knight)) {
            this.knight = null;
            this.defeated = true;
            setDirty();
        }
    }
}
