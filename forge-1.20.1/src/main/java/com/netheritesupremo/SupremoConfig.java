package com.netheritesupremo;

import net.minecraftforge.common.ForgeConfigSpec;

public final class SupremoConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue REQUIRE_FULL_SET;
    public static final ForgeConfigSpec.BooleanValue KNIGHT_RESPAWNS_AFTER_DEATH;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        REQUIRE_FULL_SET = builder
                .comment("true = escudo contra flechas, Resistencia ao Fogo e Forca II so com as 4 pecas de netherite.",
                        "false = qualquer peca de netherite vestida ativa esses efeitos.")
                .define("requireFullSet", true);
        KNIGHT_RESPAWNS_AFTER_DEATH = builder
                .comment("Existe no maximo UM cavaleiro de netherite por mundo (spawn natural).",
                        "true = depois que ele morre, outro pode nascer naturalmente de novo.",
                        "false = depois que ele morre, nunca mais nasce outro naturalmente.")
                .define("knightRespawnsAfterDeath", true);
        SPEC = builder.build();
    }

    private SupremoConfig() {
    }
}
