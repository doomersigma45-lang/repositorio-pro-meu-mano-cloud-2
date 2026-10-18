package com.netheritesupremo;

import net.minecraftforge.common.ForgeConfigSpec;

public final class SupremoConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue REQUIRE_FULL_SET;
    public static final ForgeConfigSpec.BooleanValue KNIGHT_RESPAWNS_AFTER_DEATH;
    public static final ForgeConfigSpec.BooleanValue LETHAL_HIT_PROTECTION;
    public static final ForgeConfigSpec.BooleanValue BOMB_ROBOT_DESTROYS_BLOCKS;
    public static final ForgeConfigSpec.DoubleValue RAINBOW_EXPLOSION_POWER;
    public static final ForgeConfigSpec.BooleanValue RAINBOW_DESTROYS_BLOCKS;
    public static final ForgeConfigSpec.BooleanValue PRECURSOR_DESTROYS_BLOCKS;

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
        RAINBOW_EXPLOSION_POWER = builder
                .comment("Forca das explosoes do boss Rainbow e da joia colorida. Padrao 225 = 75x a de um creeper (3).",
                        "Explosoes desse tamanho destroem uma area enorme e podem causar travadas: diminua se precisar.")
                .defineInRange("rainbowExplosionPower", 225.0D, 1.0D, 500.0D);
        RAINBOW_DESTROYS_BLOCKS = builder
                .comment("true = as explosoes do boss Rainbow (respeitando mobGriefing) e da joia colorida destroem blocos.",
                        "false = so causam dano.")
                .define("rainbowDestroysBlocks", true);
        PRECURSOR_DESTROYS_BLOCKS = builder
                .comment("true = o Precursor quebra qualquer bloco por onde passa e suas explosoes destroem blocos.",
                        "false = so causa dano.")
                .define("precursorDestroysBlocks", true);
        SPEC = builder.build();
    }

    private SupremoConfig() {
    }
}
