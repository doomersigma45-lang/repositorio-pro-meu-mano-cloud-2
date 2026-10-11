package com.netheritesupremo;

import com.mojang.logging.LogUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.slf4j.Logger;

/**
 * O atributo de armadura do jogo e limitado a 30 pontos (RangedAttribute com maximo 30).
 * Aqui o maximo e elevado por reflexao, sem depender de nomes ofuscados: procuramos o campo
 * double nao estatico cujo valor e 30.0 na instancia de Attributes.ARMOR.
 */
public final class ArmorCap {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final double NEW_MAX = 1024.0D;

    private ArmorCap() {
    }

    public static void raise() {
        Attribute armor = Attributes.ARMOR;
        if (!(armor instanceof RangedAttribute)) {
            LOGGER.warn("[NetheriteSupremo] Attributes.ARMOR nao e um RangedAttribute; teto de armadura mantido.");
            return;
        }
        boolean raised = false;
        try {
            for (Field field : RangedAttribute.class.getDeclaredFields()) {
                if (field.getType() != double.class || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                if (field.getDouble(armor) == 30.0D) {
                    field.setDouble(armor, NEW_MAX);
                    raised = true;
                }
            }
        } catch (Throwable t) {
            LOGGER.error("[NetheriteSupremo] Falha ao remover o teto de armadura", t);
        }
        if (raised) {
            LOGGER.info("[NetheriteSupremo] Teto de armadura elevado de 30 para {}", NEW_MAX);
        } else {
            LOGGER.warn("[NetheriteSupremo] Nao foi possivel elevar o teto de armadura (o limite de 30 continua).");
        }
    }
}
