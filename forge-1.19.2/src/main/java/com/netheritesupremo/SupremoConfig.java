package com.netheritesupremo;

import net.minecraftforge.common.ForgeConfigSpec;

public final class SupremoConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue REQUIRE_FULL_SET;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        REQUIRE_FULL_SET = builder
                .comment("true = escudo contra flechas, Resistencia ao Fogo e Forca II so com as 4 pecas de netherite.",
                        "false = qualquer peca de netherite vestida ativa esses efeitos.")
                .define("requireFullSet", true);
        SPEC = builder.build();
    }

    private SupremoConfig() {
    }
}
