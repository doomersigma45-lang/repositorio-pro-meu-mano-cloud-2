package com.netheritesupremo.parasite;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

/** Estado do surto: o dia (tempo do mundo) em que passou de 10 infectados pela primeira vez. */
public class ParasiteOutbreak extends SavedData {
    public static final int THRESHOLD = 10;
    private static final String NAME = "netheritesupremo_parasite_outbreak";

    /** -1 = o surto ainda nao comecou. */
    public long start = -1L;

    /** Atualizados pelo servidor a cada 5 s (leitura rapida pelos eventos de spawn). */
    public static volatile boolean active;
    public static volatile int infected;
    public static volatile double days;

    public static ParasiteOutbreak get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(ParasiteOutbreak::load, ParasiteOutbreak::new, NAME);
    }

    public static ParasiteOutbreak load(CompoundTag tag) {
        ParasiteOutbreak o = new ParasiteOutbreak();
        o.start = tag.contains("Start") ? tag.getLong("Start") : -1L;
        return o;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putLong("Start", this.start);
        return tag;
    }

    /** Fase do surto: dias 0-5 = 1, 5-11 = 2, 11 em diante = 3. */
    public static int stage() {
        return days < 5.0D ? 1 : (days < 11.0D ? 2 : 3);
    }
}
