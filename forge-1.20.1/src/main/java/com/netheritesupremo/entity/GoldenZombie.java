package com.netheritesupremo.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

/**
 * Zumbi dourado: um zumbi comum (mesma IA, modelo e armadura) com 27 de vida, 8 de dano e mais
 * rapido que o zumbi normal (0.23). Dropa blocos de ouro (ver o loot table).
 */
public class GoldenZombie extends Zombie {
    public static final double MAX_HEALTH = 27.0D;
    public static final double ATTACK_DAMAGE = 8.0D;
    public static final double MOVEMENT_SPEED = 0.30D;

    public GoldenZombie(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createGoldenZombieAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED);
    }

    /** Nao vira afogado na agua: continua sendo um zumbi dourado. */
    @Override
    protected boolean convertsInWater() {
        return false;
    }
}
