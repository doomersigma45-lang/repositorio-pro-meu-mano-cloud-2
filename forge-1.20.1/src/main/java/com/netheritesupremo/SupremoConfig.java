package com.netheritesupremo;

import net.minecraftforge.common.ForgeConfigSpec;

public final class SupremoConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue REQUIRE_FULL_SET;
    public static final ForgeConfigSpec.BooleanValue KNIGHT_RESPAWNS_AFTER_DEATH;
    public static final ForgeConfigSpec.BooleanValue LETHAL_HIT_PROTECTION;
    public static final ForgeConfigSpec.BooleanValue BOMB_ROBOT_DESTROYS_BLOCKS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        REQUIRE_FULL_SET = builder
                .comment("true = escudo contra flechas, Resistencia ao Fogo e Forca II so com as 4 pecas de netherite.",
                        "false = qualquer peca de netherite vestida ativa esses efeitos.")
                .define("requireFullSet", true);
        KNIGHT_RESPAWNS_AFTER_DEATH = builder
                .comment("Existe no maximo UM grupo (10) de cavaleiros de netherite selvagens por mundo (spawn natural).",
                        "true = depois que todos morrem, outro grupo pode nascer naturalmente de novo.",
                        "false = depois que todos morrem, nunca mais nasce outro grupo naturalmente.")
                .define("knightRespawnsAfterDeath", true);
        LETHAL_HIT_PROTECTION = builder
                .comment("true = jogador com a armadura de netherite completa tem anulado qualquer golpe unico que o mataria",
                        "(dano final >= vida atual e >= metade da vida maxima). Nao vale para vazio nem /kill.")
                .define("lethalHitProtection", true);
        BOMB_ROBOT_DESTROYS_BLOCKS = builder
                .comment("true = a explosao de morte do bomb-robot (forca 30, 10x a de um creeper) destroi blocos",
                        "(respeitando a regra mobGriefing). false = so causa dano, sem destruir blocos.")
                .define("bombRobotDestroysBlocks", true);
        SPEC = builder.build();
    }

    private SupremoConfig() {
    }
}
