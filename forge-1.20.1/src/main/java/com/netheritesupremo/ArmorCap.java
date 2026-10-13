package com.netheritesupremo;

import com.mojang.logging.LogUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.slf4j.Logger;

/**
 * Remove limites de atributos do jogo, que sao RangedAttribute com maximo fixo:
 * armadura (30) e vida maxima (1024, o golem de netherite tem 1800).
 * O maximo e elevado por reflexao, sem depender de nomes ofuscados: procuramos o campo
 * double nao estatico cujo valor e o maximo antigo na instancia do atributo.
 */
public final class ArmorCap {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final double ARMOR_MAX = 1024.0D;
    public static final double HEALTH_MAX = 100000.0D;

    private ArmorCap() {
    }

    public static void raise() {
        raise(Attributes.ARMOR, "armadura", 30.0D, ARMOR_MAX);
        raise(Attributes.MAX_HEALTH, "vida maxima", 1024.0D, HEALTH_MAX);
    }

    private static void raise(Attribute attribute, String name, double oldMax, double newMax) {
        if (!(attribute instanceof RangedAttribute)) {
            LOGGER.warn("[NetheriteSupremo] Atributo {} nao e um RangedAttribute; teto mantido.", name);
            return;
        }
        boolean raised = false;
        try {
            for (Field field : RangedAttribute.class.getDeclaredFields()) {
                if (field.getType() != double.class || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                if (field.getDouble(attribute) == oldMax) {
                    field.setDouble(attribute, newMax);
                    raised = true;
                }
            }
        } catch (Throwable t) {
            LOGGER.error("[NetheriteSupremo] Falha ao remover o teto de " + name, t);
        }
        if (raised) {
            LOGGER.info("[NetheriteSupremo] Teto de {} elevado de {} para {}", name, oldMax, newMax);
        } else {
            LOGGER.warn("[NetheriteSupremo] Nao foi possivel elevar o teto de {} (continua {}).", name, oldMax);
        }
    }
}
