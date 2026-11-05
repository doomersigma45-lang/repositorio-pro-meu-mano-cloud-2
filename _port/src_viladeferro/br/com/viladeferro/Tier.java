/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.animal.IronGolem
 */
package br.com.viladeferro;

import br.com.viladeferro.FighterVillager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.IronGolem;

public enum Tier {
    NATURAL,
    AGGRESSIVE,
    PASSIVE;

    private static final String KEY = "viladeferro_tier";

    public static Tier of(Entity e) {
        if (e instanceof FighterVillager) {
            FighterVillager f = (FighterVillager)e;
            return f.getTier();
        }
        if (e instanceof IronGolem) {
            String s = e.getPersistentData().m_128461_(KEY);
            for (Tier t : Tier.values()) {
                if (!t.name().equals(s)) continue;
                return t;
            }
        }
        return NATURAL;
    }

    public static void set(IronGolem golem, Tier tier) {
        golem.getPersistentData().m_128359_(KEY, tier.name());
    }
}

