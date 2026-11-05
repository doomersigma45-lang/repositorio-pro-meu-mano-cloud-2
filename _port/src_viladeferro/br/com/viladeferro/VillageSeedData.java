/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.saveddata.SavedData
 */
package br.com.viladeferro;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class VillageSeedData
extends SavedData {
    private static final String NAME = "viladeferro_villages";
    private final List<BlockPos> seeded = new ArrayList<BlockPos>();

    public static VillageSeedData get(ServerLevel level) {
        return (VillageSeedData)level.m_8895_().m_164861_(VillageSeedData::load, VillageSeedData::new, NAME);
    }

    public boolean isSeeded(BlockPos pos) {
        for (BlockPos p : this.seeded) {
            if (!(p.m_123331_((Vec3i)pos) < 16384.0)) continue;
            return true;
        }
        return false;
    }

    public void add(BlockPos pos) {
        this.seeded.add(pos.m_7949_());
        this.m_77762_();
    }

    public CompoundTag m_7176_(CompoundTag tag) {
        tag.m_128388_("seeded", this.seeded.stream().mapToLong(BlockPos::m_121878_).toArray());
        return tag;
    }

    public static VillageSeedData load(CompoundTag tag) {
        VillageSeedData data = new VillageSeedData();
        for (long l : tag.m_128467_("seeded")) {
            data.seeded.add(BlockPos.m_122022_((long)l));
        }
        return data;
    }
}

