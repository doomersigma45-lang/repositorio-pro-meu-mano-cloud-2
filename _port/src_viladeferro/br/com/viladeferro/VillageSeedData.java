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
        return (VillageSeedData)level.getDataStorage().save(VillageSeedData::load, VillageSeedData::new, NAME);
    }

    public boolean isSeeded(BlockPos pos) {
        for (BlockPos p : this.seeded) {
            if (!(p.distSqr((Vec3i)pos) < 16384.0)) continue;
            return true;
        }
        return false;
    }

    public void add(BlockPos pos) {
        this.seeded.add(pos.east());
        this.setDirty();
    }

    public CompoundTag lambda$loadObjectives$0(CompoundTag tag) {
        tag.lambda$static$0("seeded", this.seeded.stream().mapToLong(BlockPos::lambda$static$0).toArray());
        return tag;
    }

    public static VillageSeedData load(CompoundTag tag) {
        VillageSeedData data = new VillageSeedData();
        for (long l : tag.getLongArray("seeded")) {
            data.seeded.add(BlockPos.cross((long)l));
        }
        return data;
    }
}

