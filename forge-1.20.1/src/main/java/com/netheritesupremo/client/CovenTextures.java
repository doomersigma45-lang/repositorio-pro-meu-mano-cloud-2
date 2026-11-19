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
 * Texturas das criaturas do covil e dos novos parasitas, geradas na hora: as do jogo sao recoloridas por um mapa de
 * luminosidade (o desenho de cada parte continua certo) e as dos modelos proprios sao pintadas por codigo.
 * Recriadas a cada recarga de recursos.
 */
public class CovenTextures implements ResourceManagerReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceLocation IMP = gen("imp");
    public static final ResourceLocation WEREWOLF = gen("werewolf");
    public static final ResourceLocation SHADOW_PERSON = gen("shadow_person");
    public static final ResourceLocation GHOST = gen("ghost");
    public static final ResourceLocation DEMON = gen("demon");
    public static final ResourceLocation CAMBION = gen("cambion");
    public static final ResourceLocation SNAKE = gen("snake");
    public static final ResourceLocation BLACK_DOG = gen("black_dog");
    public static final ResourceLocation HELLHOUND = gen("hellhound");
    public static final ResourceLocation RAVEN = gen("raven");
    public static final ResourceLocation OWL = gen("owl");
    public static final ResourceLocation TOAD = gen("toad");
    public static final ResourceLocation PARASITE_BLAZE = gen("parasite_blaze");
    public static final ResourceLocation PARASITE_BOMBER = gen("parasite_bomber");
    public static final ResourceLocation PARASITE_HEAD = gen("parasite_head");
    public static final ResourceLocation PARASITE_GNAT = gen("parasite_gnat");
    public static final ResourceLocation PARASITE_ROOTER = gen("parasite_rooter");

    private static ResourceLocation gen(String name) {
        return new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/" + name + "_generated");
    }

    @Override
    public void onResourceManagerReload(ResourceManager m) {
        ResourceLocation zombie = new ResourceLocation("textures/entity/zombie/zombie.png");
        ResourceLocation wolf = new ResourceLocation("textures/entity/wolf/wolf.png");
        ResourceLocation parrot = new ResourceLocation("textures/entity/parrot/parrot_grey.png");
        tint(m, zombie, IMP, new int[]{60, 6, 6}, new int[]{235, 90, 50}, 10, 255, false);
        tint(m, zombie, WEREWOLF, new int[]{22, 16, 12}, new int[]{125, 92, 64}, 26, 255, false);
        tint(m, zombie, SHADOW_PERSON, new int[]{0, 0, 0}, new int[]{20, 18, 26}, 3, 255, true);
        tint(m, zombie, GHOST, new int[]{150, 170, 205}, new int[]{245, 250, 255}, 6, 150, false);
        tint(m, zombie, DEMON, new int[]{24, 0, 0}, new int[]{170, 24, 20}, 14, 255, false);
        tint(m, zombie, CAMBION, new int[]{26, 10, 34}, new int[]{205, 160, 175}, 8, 255, false);
        tint(m, new ResourceLocation("textures/entity/silverfish.png"), SNAKE, new int[]{18, 48, 10}, new int[]{160, 180, 44}, 20, 255, false);
        tint(m, wolf, BLACK_DOG, new int[]{4, 4, 6}, new int[]{52, 50, 60}, 6, 255, false);
        tint(m, wolf, HELLHOUND, new int[]{34, 6, 4}, new int[]{230, 90, 24}, 20, 255, false);
        tint(m, parrot, RAVEN, new int[]{2, 2, 6}, new int[]{66, 66, 84}, 5, 255, false);
        tint(m, parrot, OWL, new int[]{60, 40, 24}, new int[]{238, 218, 184}, 14, 255, false);
        tint(m, new ResourceLocation("textures/entity/frog/temperate_frog.png"), TOAD, new int[]{30, 28, 14}, new int[]{150, 128, 66}, 24, 255, false);
        tint(m, new ResourceLocation("textures/entity/blaze.png"), PARASITE_BLAZE, new int[]{40, 6, 10}, new int[]{235, 70, 40}, 22, 255, false);
        tint(m, new ResourceLocation("textures/entity/phantom.png"), PARASITE_BOMBER, new int[]{34, 10, 16}, new int[]{190, 90, 70}, 20, 255, false);
        paintHead();
        paintGnat();
        paintRooter();
    }

    private static void register(ResourceLocation target, NativeImage image) {
        Minecraft.getInstance().getTextureManager().register(target, new DynamicTexture(image));
    }

    private static int hash(int x, int y) {
        int h = x * 374761393 + y * 668265263;
        h = (h ^ (h >>> 13)) * 1274126177;
        return (h ^ (h >>> 16)) & 0x7FFFFFFF;
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }

    private static int pack(int a, int r, int g, int b) {
        return (clamp(a) << 24) | (clamp(b) << 16) | (clamp(g) << 8) | clamp(r);
    }

    /** Mapa de luminosidade: o escuro do original vira "lo", o claro vira "hi", com ruido para parecer pelo/carne. */
    private static void tint(ResourceManager manager, ResourceLocation source, ResourceLocation target, int[] lo, int[] hi,
            int noise, int maxAlpha, boolean eyes) {
        NativeImage image;
        try (InputStream in = manager.getResourceOrThrow(source).open()) {
            image = NativeImage.read(in);
        } catch (IOException | RuntimeException e) {
            LOGGER.error("[NetheriteSupremo] Nao foi possivel ler a textura {}", source, e);
            return;
        }
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
                float lum = Math.min(1.0F, (0.299F * r + 0.587F * g + 0.114F * b) / 200.0F);
                int n = noise > 0 ? (hash(x, y) % (noise * 2 + 1)) - noise : 0;
                image.setPixelRGBA(x, y, pack(Math.min(alpha, maxAlpha),
                        (int) (lo[0] + (hi[0] - lo[0]) * lum) + n,
                        (int) (lo[1] + (hi[1] - lo[1]) * lum) + n,
                        (int) (lo[2] + (hi[2] - lo[2]) * lum) + n));
            }
        }
        if (eyes && image.getWidth() >= 64 && image.getHeight() >= 32) {
            for (int x : new int[]{9, 10, 13, 14}) { // olhos brancos na face da cabeca (layout de pele de 64x64)
                image.setPixelRGBA(x, 12, pack(255, 255, 255, 255));
                image.setPixelRGBA(x, 13, pack(255, 235, 235, 235));
            }
        }
        register(target, image);
    }

    /** Preenche tudo com carne manchada; base (r,g,b) com variacao por ruido. */
    private static NativeImage flesh(int w, int h, int r, int g, int b, int noise) {
        NativeImage image = new NativeImage(w, h, true);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int n = (hash(x, y) % (noise * 2 + 1)) - noise;
                int blotch = hash(x / 3, y / 3) % 6 == 0 ? 30 : 0;
                image.setPixelRGBA(x, y, pack(255, r + n + blotch, g + n - blotch / 2, b + n - blotch / 2));
            }
        }
        return image;
    }

    private static void rect(NativeImage image, int x0, int y0, int w, int h, int r, int g, int b) {
        for (int y = y0; y < y0 + h && y < image.getHeight(); y++) {
            for (int x = x0; x < x0 + w && x < image.getWidth(); x++) {
                image.setPixelRGBA(x, y, pack(255, r, g, b));
            }
        }
    }

    /** Cabeca Andante (64x32): pele palida com veias, olhos vermelhos e boca cheia de dentes na face da frente. */
    private static void paintHead() {
        NativeImage img = flesh(64, 32, 188, 150, 134, 14);
        rect(img, 0, 18, 8, 7, 120, 40, 48); // perninhas mais escuras
        rect(img, 12, 13, 2, 2, 230, 20, 20); // olhos (face da frente: x 10..20, y 10..18)
        rect(img, 17, 13, 2, 2, 230, 20, 20);
        rect(img, 12, 16, 6, 2, 60, 6, 10); // boca
        for (int x = 12; x < 18; x += 2) {
            img.setPixelRGBA(x, 16, pack(255, 236, 228, 210)); // dentes
        }
        register(PARASITE_HEAD, img);
    }

    /** Mosquito (64x32): corpo escuro, olhos vermelhos e asas claras. */
    private static void paintGnat() {
        NativeImage img = flesh(64, 32, 34, 26, 30, 8);
        rect(img, 3, 11, 1, 1, 235, 30, 30); // olhos na face da frente da cabeca
        rect(img, 5, 11, 1, 1, 235, 30, 30);
        rect(img, 20, 8, 18, 4, 196, 196, 208); // asas
        register(PARASITE_GNAT, img);
    }

    /** Rooter (64x64): carne vermelho-escura com veias, maxilas de osso. */
    private static void paintRooter() {
        NativeImage img = flesh(64, 64, 112, 30, 42, 16);
        for (int i = 0; i < 40; i++) { // veias escuras
            int x = hash(i, 7) % 64;
            int y = hash(i, 11) % 40;
            rect(img, x, y, 1, 3 + hash(i, 3) % 5, 54, 8, 20);
        }
        rect(img, 0, 40, 64, 11, 214, 200, 170); // maxilas (osso)
        for (int x = 0; x < 64; x += 4) {
            rect(img, x, 48, 2, 3, 120, 20, 30); // sulcos entre os dentes
        }
        register(PARASITE_ROOTER, img);
    }
}
