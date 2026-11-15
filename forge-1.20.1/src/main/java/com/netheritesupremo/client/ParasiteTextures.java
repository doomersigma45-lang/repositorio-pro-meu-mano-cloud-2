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

    private static int hash(int x, int y) {
        int h = x * 73856093 ^ y * 19349663;
        h ^= h >>> 13;
        h *= 0x5BD1E995;
        h ^= h >>> 15;
        return h & 0x7FFFFFFF;
    }
}
