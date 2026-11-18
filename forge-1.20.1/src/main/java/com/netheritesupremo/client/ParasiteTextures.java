package com.netheritesupremo.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import com.netheritesupremo.NetheriteSupremo;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.slf4j.Logger;

/**
 * Texturas dos infectados grandes (golem de ferro, golem de netherite, Warden e Wither), geradas a partir das
 * texturas do proprio jogo: o desenho de cada parte (UV) continua certo, so as cores viram carne, sangue e osso.
 * Recriadas a cada recarga de recursos.
 */
public class ParasiteTextures implements ResourceManagerReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceLocation IRON_GOLEM = gen("parasite_iron_golem_generated");
    public static final ResourceLocation NETHERITE_GOLEM = gen("parasite_netherite_golem_generated");
    public static final ResourceLocation WARDEN = gen("parasite_warden_generated");
    public static final ResourceLocation WARDEN_GLOW = gen("parasite_warden_glow_generated");
    public static final ResourceLocation WITHER = gen("parasite_wither_generated");
    public static final ResourceLocation SHEEP = gen("parasite_sheep_generated");
    public static final ResourceLocation WOLF = gen("parasite_wolf_generated");
    public static final ResourceLocation STRONG_ZOMBIE = gen("parasite_strongest_zombie_generated");
    public static final ResourceLocation HUNTER = gen("parasite_hunter_generated");
    public static final ResourceLocation HYBRID_CRAWLER = gen("parasite_crawler_generated");
    public static final ResourceLocation HYBRID_HOUND = gen("parasite_hound_generated");
    public static final ResourceLocation HYBRID_MOTHER = gen("parasite_mother_generated");
    public static final ResourceLocation HYBRID_CHIMERA = gen("parasite_chimera_generated");
    public static final ResourceLocation HYBRID_AMALGAM = gen("parasite_amalgam_generated");
    public static final ResourceLocation SQUID = gen("parasite_squid_generated");
    public static final ResourceLocation GLOW_SQUID = gen("parasite_glow_squid_generated");
    public static final ResourceLocation SILVERFISH = gen("parasite_silverfish_generated");
    public static final ResourceLocation HYBRID_BURSTER = gen("parasite_burster_generated");
    public static final ResourceLocation WITCH = gen("parasite_witch_generated");
    public static final ResourceLocation HYBRID_GLOW = gen("parasite_hybrid_glow_generated");
    public static final ResourceLocation VILLAGER = gen("parasite_villager_generated");
    public static final ResourceLocation VILLAGER_EYES = gen("parasite_villager_eyes_generated");
    public static final ResourceLocation TRADER = gen("parasite_trader_generated");
    public static final ResourceLocation CLAWS = gen("parasite_claws_generated");

    private static ResourceLocation gen(String name) {
        return new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/" + name);
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        build(manager, new ResourceLocation("textures/entity/iron_golem/iron_golem.png"), IRON_GOLEM, 0);
        build(manager, new ResourceLocation("textures/entity/iron_golem/iron_golem.png"), NETHERITE_GOLEM, 1);
        build(manager, new ResourceLocation("textures/entity/warden/warden.png"), WARDEN, 2);
        build(manager, new ResourceLocation("textures/entity/warden/warden_bioluminescent_layer.png"), WARDEN_GLOW, 3);
        build(manager, new ResourceLocation("textures/entity/wither/wither.png"), WITHER, 4);
        build(manager, new ResourceLocation("textures/entity/sheep/sheep.png"), SHEEP, 0);
        build(manager, new ResourceLocation("textures/entity/wolf/wolf.png"), WOLF, 0);
        build(manager, new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/strongest_zombie.png"), STRONG_ZOMBIE, 0);
        build(manager, new ResourceLocation("textures/entity/zombie_villager/zombie_villager.png"), VILLAGER, 5);
        build(manager, new ResourceLocation("textures/entity/zombie_villager/zombie_villager.png"), TRADER, 6);
        build(manager, new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/hunter.png"), HUNTER, 0);
        buildHybrid(HYBRID_CRAWLER, 128, 92, 84);
        buildHybrid(HYBRID_HOUND, 104, 50, 44);
        buildHybrid(HYBRID_MOTHER, 92, 36, 62);
        buildHybrid(HYBRID_CHIMERA, 96, 62, 52);
        buildHybrid(HYBRID_AMALGAM, 70, 30, 40);
        buildHybrid(HYBRID_BURSTER, 122, 104, 52);
        build(manager, new ResourceLocation("textures/entity/squid/squid.png"), SQUID, 0);
        build(manager, new ResourceLocation("textures/entity/squid/glow_squid.png"), GLOW_SQUID, 0);
        build(manager, new ResourceLocation("textures/entity/silverfish.png"), SILVERFISH, 0);
        build(manager, new ResourceLocation("textures/entity/witch.png"), WITCH, 0);
        buildHybridGlow();
        buildEyes();
        buildClaws();
    }

    private static void build(ResourceManager manager, ResourceLocation source, ResourceLocation target, int style) {
        NativeImage image;
        try (InputStream in = manager.getResourceOrThrow(source).open()) {
            image = NativeImage.read(in);
        } catch (IOException | RuntimeException e) {
            LOGGER.error("[NetheriteSupremo] Nao foi possivel ler a textura {}", source, e);
            return;
        }
        recolor(image, style);
        if (style >= 5) {
            decorateVillager(image, style);
        }
        Minecraft.getInstance().getTextureManager().register(target, new DynamicTexture(image));
    }

    private static int pack(int alpha, int r, int g, int b) {
        return (alpha << 24) | (clamp(b) << 16) | (clamp(g) << 8) | clamp(r);
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }

    /** style: 0 golem de ferro, 1 golem de netherite, 2 warden, 3 brilho do warden, 4 wither. */
    private static void recolor(NativeImage image, int style) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = image.getPixelRGBA(x, y); // ABGR
                int alpha = (pixel >>> 24) & 0xFF;
                if (alpha == 0) {
                    continue;
                }
                int r = pixel & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = (pixel >> 16) & 0xFF;
                float lum = (0.299F * r + 0.587F * g + 0.114F * b) / 255.0F;
                int h = hash(x, y);
                int h2 = hash(x / 3, y / 3);
                int nr;
                int ng;
                int nb;
                switch (style) {
                    case 0 -> { // pele clara manchada de sangue
                        if (lum > 0.55F) {
                            nr = 214 - (h % 40);
                            ng = 206 - (h % 44);
                            nb = 190 - (h % 48);
                        } else if (lum > 0.3F) {
                            nr = 150 + (h % 40);
                            ng = 22 + (h % 18);
                            nb = 26 + (h % 18);
                        } else {
                            nr = 78 + (h % 20);
                            ng = 10;
                            nb = 14;
                        }
                        if (h2 % 5 == 0) { // manchas de carne viva
                            nr = 196 + (h % 40);
                            ng = 36;
                            nb = 40;
                        }
                        if (h % 29 == 0) { // tentaculos pretos
                            nr = 14;
                            ng = 12;
                            nb = 14;
                        }
                    }
                    case 1 -> { // netherite carbonizado, sangue e brasas
                        nr = (int) (26 + lum * 70);
                        ng = (int) (22 + lum * 60);
                        nb = (int) (24 + lum * 62);
                        if (h2 % 4 == 0) {
                            nr = 128 + (h % 44);
                            ng = 16;
                            nb = 20;
                        }
                        if (h % 27 == 0) {
                            nr = 236;
                            ng = 96;
                            nb = 22;
                        }
                        if (h % 31 == 0) {
                            nr = 12;
                            ng = 10;
                            nb = 12;
                        }
                    }
                    case 2 -> { // warden: cinza escuro, o azul-turquesa vira vermelho
                        boolean teal = b > r + 20 && g > r;
                        if (teal) {
                            nr = (int) (120 + lum * 130);
                            ng = (int) (16 + lum * 24);
                            nb = (int) (24 + lum * 30);
                        } else {
                            nr = (int) (26 + lum * 78);
                            ng = (int) (26 + lum * 72);
                            nb = (int) (30 + lum * 76);
                            if (h2 % 6 == 0) {
                                nr = 150 + (h % 40);
                                ng = 24;
                                nb = 30;
                            }
                        }
                    }
                    case 3 -> { // brilho do warden em vermelho
                        nr = (int) Math.min(255, 150 + lum * 140);
                        ng = (int) (20 + lum * 40);
                        nb = (int) (24 + lum * 36);
                    }
                    case 5, 6 -> { // aldeao: pele de carne, roupa marrom (5) ou azul do vendedor (6)
                        boolean skinZone = x < 32 && y < 18;
                        boolean green = g > r + 6;
                        if (green || skinZone) {
                            if (lum < 0.22F) {
                                nr = 42;
                                ng = 24;
                                nb = 26;
                            } else {
                                nr = (int) (110 + lum * 120);
                                ng = (int) (70 + lum * 80);
                                nb = (int) (62 + lum * 70);
                            }
                            if (h2 % 11 == 0) {
                                nr = 112 + (h % 30);
                                ng = 24;
                                nb = 30;
                            }
                            if (h % 41 == 0) {
                                nr = 70;
                                ng = 16;
                                nb = 20;
                            }
                        } else if (style == 5) {
                            nr = (int) (60 + lum * 90);
                            ng = (int) (44 + lum * 60);
                            nb = (int) (38 + lum * 50);
                            if (h2 % 6 == 0) {
                                nr = 120 + (h % 40);
                                ng = 16;
                                nb = 20;
                            }
                        } else {
                            nr = (int) (24 + lum * 60);
                            ng = (int) (40 + lum * 80);
                            nb = (int) (96 + lum * 120);
                            if (h2 % 8 == 0) {
                                nr = 120 + (h % 40);
                                ng = 16;
                                nb = 20;
                            }
                        }
                        if (h % 53 == 0) {
                            nr = 40;
                            ng = 10;
                            nb = 14;
                        }
                    }
                    default -> { // wither: osso sujo, carne e preto
                        if (lum > 0.6F) {
                            nr = 200 - (h % 36);
                            ng = 192 - (h % 40);
                            nb = 176 - (h % 40);
                        } else if (lum > 0.25F) {
                            nr = 70 + (h % 20);
                            ng = 44;
                            nb = 46;
                        } else {
                            nr = 20;
                            ng = 16;
                            nb = 18;
                        }
                        if (h2 % 6 == 0) {
                            nr = 160 + (h % 40);
                            ng = 22;
                            nb = 28;
                        }
                    }
                }
                image.setPixelRGBA(x, y, pack(alpha, nr, ng, nb));
            }
        }
    }

    private static void px(NativeImage image, int x, int y, int r, int g, int b) {
        image.setPixelRGBA(x, y, pack(255, r, g, b));
    }

    /** Boca de dentes afiados, olhos vermelhos e, no vendedor, o capuz azul (pintados direto na cabeca do modelo). */
    private static void decorateVillager(NativeImage image, int style) {
        if (style == 6) { // capuz: topo e nuca inteiros, laterais ate a linha 13, testa ate a linha 10
            for (int y = 0; y < 18; y++) {
                for (int x = 0; x < 32; x++) {
                    boolean top = x >= 8 && x < 16 && y < 8;
                    boolean back = x >= 24 && y >= 8;
                    boolean side = (x < 8 || x >= 16 && x < 24) && y >= 8 && y <= 13;
                    boolean front = x >= 8 && x < 16 && y >= 8 && y <= 10;
                    if (top || back || side || front) {
                        int h = hash(x, y) % 16;
                        boolean trim = (side && y == 13) || (front && y == 10);
                        if (trim) {
                            px(image, x, y, 96 + h, 120 + h, 200);
                        } else {
                            px(image, x, y, 38 + h, 56 + h, 140 + h);
                        }
                    }
                }
            }
        }
        // face frontal: x 8..15, y 8..17. Sobrancelhas inclinadas na linha 11, olhos nas 12-13, boca nas 15-17
        for (int x : new int[] {8, 9, 10, 13, 14, 15}) {
            px(image, x, 11, 36, 8, 10);
        }
        px(image, 9, 12, 30, 6, 8);
        px(image, 14, 12, 30, 6, 8);
        for (int[] e : EYES) {
            px(image, e[0], e[1], 255, 40, 40);
        }
        for (int x = 8; x <= 15; x++) { // boca escura, fileira de dentes em cima e presas
            px(image, x, 15, 232, 226, 214);
            boolean fang = x == 8 || x == 10 || x == 13 || x == 15;
            px(image, x, 16, fang ? 232 : 46, fang ? 226 : 6, fang ? 214 : 10);
            boolean low = x == 9 || x == 14;
            px(image, x, 17, low ? 232 : 46, low ? 226 : 6, low ? 214 : 10);
        }
    }

    /** Olhos inclinados (zangados): 3 pixels cada, o canto interno mais baixo. */
    private static final int[][] EYES = {{10, 12}, {9, 13}, {10, 13}, {13, 12}, {13, 13}, {14, 13}};

    /** Textura so com os olhos, desenhada com brilho total por cima do modelo. */
    private static void buildEyes() {
        NativeImage image = new NativeImage(64, 64, true);
        for (int[] e : EYES) {
            image.setPixelRGBA(e[0], e[1], pack(255, 255, 60, 60));
        }
        Minecraft.getInstance().getTextureManager().register(VILLAGER_EYES, new DynamicTexture(image));
    }

    private static void fill(NativeImage image, int x0, int y0, int w, int h, int r, int g, int b) {
        for (int y = y0; y < y0 + h; y++) {
            for (int x = x0; x < x0 + w; x++) {
                image.setPixelRGBA(x, y, pack(255, r, g, b));
            }
        }
    }

    /** Carne manchada (128x128) com tres retalhos lisos no canto: olho (x120), osso (x112) e boca escura (x104). */
    private static void buildHybrid(ResourceLocation target, int br, int bg, int bb) {
        NativeImage image = new NativeImage(128, 128, true);
        for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 128; x++) {
                int h = hash(x, y);
                int h2 = hash(x / 3, y / 3);
                int v = (h % 30) - 15;
                int r = br + v;
                int g = bg + v / 2;
                int b = bb + v / 2;
                if (h2 % 6 == 0) {
                    r = (int) (r * 0.55F);
                    g = (int) (g * 0.5F);
                    b = (int) (b * 0.5F);
                } else if (h2 % 9 == 1) {
                    r += 50;
                    g = Math.max(0, g - 10);
                }
                if (h % 47 == 0) {
                    r = 40;
                    g = 10;
                    b = 14;
                }
                image.setPixelRGBA(x, y, pack(255, r, g, b));
            }
        }
        fill(image, 120, 0, 8, 8, 255, 50, 50);
        fill(image, 112, 0, 8, 8, 226, 218, 200);
        fill(image, 104, 0, 8, 8, 30, 6, 10);
        Minecraft.getInstance().getTextureManager().register(target, new DynamicTexture(image));
    }

    private static void buildHybridGlow() {
        NativeImage image = new NativeImage(128, 128, true);
        fill(image, 120, 0, 8, 8, 255, 60, 60);
        Minecraft.getInstance().getTextureManager().register(HYBRID_GLOW, new DynamicTexture(image));
    }

    private static void buildClaws() {
        NativeImage image = new NativeImage(16, 16, true);
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                int h = hash(x, y) % 24;
                // linhas 0-7: vermelho escuro (base das garras); 8-15: osso (pontas das garras e dentes)
                image.setPixelRGBA(x, y, y < 8 ? pack(255, 96 + h, 14, 18) : pack(255, 214 - h, 204 - h, 184 - h));
            }
        }
        Minecraft.getInstance().getTextureManager().register(CLAWS, new DynamicTexture(image));
    }

    private static int hash(int x, int y) {
        int h = x * 73856093 ^ y * 19349663;
        h ^= h >>> 13;
        h *= 0x5BD1E995;
        h ^= h >>> 15;
        return h & 0x7FFFFFFF;
    }
}
