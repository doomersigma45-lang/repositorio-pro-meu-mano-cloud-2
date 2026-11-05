package com.netheritesupremo.invasion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/** Estado persistente da invasao alienigena (guardado no Overworld). */
public class InvasionData extends SavedData {
    private static final String NAME = "netheritesupremo_invasion";

    public boolean aliensWon;
    public final List<Long> cities = new ArrayList<>();

    @Nullable public UUID pending;
    public long pendingAt;

    public boolean active;
    public boolean test;
    @Nullable public UUID target;
    public BlockPos center = BlockPos.ZERO;
    public BlockPos home = BlockPos.ZERO;
    public long nextWave;
    public int wave;
    public boolean bossSpawned;
    @Nullable public UUID boss;
    public long bossTime;
    public int deaths;
    public final Set<UUID> villagers = new HashSet<>();

    public static InvasionData get(ServerLevel overworld) {
        return overworld.getDataStorage().computeIfAbsent(InvasionData::load, InvasionData::new, NAME);
    }

    public static InvasionData load(CompoundTag tag) {
        InvasionData d = new InvasionData();
        d.aliensWon = tag.getBoolean("won");
        for (long l : tag.getLongArray("cities")) {
            d.cities.add(l);
        }
        if (tag.hasUUID("pending")) {
            d.pending = tag.getUUID("pending");
        }
        d.pendingAt = tag.getLong("pendingAt");
        d.active = tag.getBoolean("active");
        d.test = tag.getBoolean("test");
        if (tag.hasUUID("target")) {
            d.target = tag.getUUID("target");
        }
        d.center = BlockPos.of(tag.getLong("center"));
        d.home = BlockPos.of(tag.getLong("home"));
        d.nextWave = tag.getLong("nextWave");
        d.wave = tag.getInt("wave");
        d.bossSpawned = tag.getBoolean("bossSpawned");
        if (tag.hasUUID("boss")) {
            d.boss = tag.getUUID("boss");
        }
        d.bossTime = tag.getLong("bossTime");
        d.deaths = tag.getInt("deaths");
        ListTag list = tag.getList("villagers", Tag.TAG_INT_ARRAY);
        for (Tag t : list) {
            d.villagers.add(NbtUtils.loadUUID(t));
        }
        return d;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("won", this.aliensWon);
        tag.putLongArray("cities", this.cities);
        if (this.pending != null) {
            tag.putUUID("pending", this.pending);
        }
        tag.putLong("pendingAt", this.pendingAt);
        tag.putBoolean("active", this.active);
        tag.putBoolean("test", this.test);
        if (this.target != null) {
            tag.putUUID("target", this.target);
        }
        tag.putLong("center", this.center.asLong());
        tag.putLong("home", this.home.asLong());
        tag.putLong("nextWave", this.nextWave);
        tag.putInt("wave", this.wave);
        tag.putBoolean("bossSpawned", this.bossSpawned);
        if (this.boss != null) {
            tag.putUUID("boss", this.boss);
        }
        tag.putLong("bossTime", this.bossTime);
        tag.putInt("deaths", this.deaths);
        ListTag list = new ListTag();
        for (UUID u : this.villagers) {
            list.add(NbtUtils.createUUID(u));
        }
        tag.put("villagers", list);
        return tag;
    }
}
