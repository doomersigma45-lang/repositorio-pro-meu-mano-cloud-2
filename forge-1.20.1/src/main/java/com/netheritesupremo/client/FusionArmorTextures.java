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
 * Gera as texturas da armadura de fusao a partir das armaduras de ferro e de diamante do proprio jogo:
 * em cada face do modelo, a metade esquerda e de ferro e a direita e de diamante. Recriadas a cada
 * recarga de recursos (acompanha resource packs).
 */
public class FusionArmorTextures implements ResourceManagerReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    /** Faces do modelo de armadura (64x32): x, y, largura, altura. Cabeca e chapeu, corpo, braco, perna. */
    private static final int[][] FACES = {
            {8, 0, 8, 8}, {16, 0, 8, 8}, {0, 8, 8, 8}, {8, 8, 8, 8}, {16, 8, 8, 8}, {24, 8, 8, 8},
            {40, 0, 8, 8}, {48, 0, 8, 8}, {32, 8, 8, 8}, {40, 8, 8, 8}, {48, 8, 8, 8}, {56, 8, 8, 8},
            {20, 16, 8, 4}, {28, 16, 8, 4}, {16, 20, 4, 12}, {20, 20, 8, 12}, {28, 20, 4, 12}, {32, 20, 8, 12},
            {44, 16, 4, 4}, {48, 16, 4, 4}, {40, 20, 4, 12}, {44, 20, 4, 12}, {48, 20, 4, 12}, {52, 20, 4, 12},
            {4, 16, 4, 4}, {8, 16, 4, 4}, {0, 20, 4, 12}, {4, 20, 4, 12}, {8, 20, 4, 12}, {12, 20, 4, 12}
    };

    public static ResourceLocation layer(int layer) {
        return new ResourceLocation(NetheriteSupremo.MODID, "textures/models/armor/fusion_layer_" + layer + ".png");
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        for (int layer = 1; layer <= 2; layer++) {
            try (InputStream ironIn = manager.getResourceOrThrow(
                            new ResourceLocation("textures/models/armor/iron_layer_" + layer + ".png")).open();
                    InputStream diamondIn = manager.getResourceOrThrow(
                            new ResourceLocation("textures/models/armor/diamond_layer_" + layer + ".png")).open();
                    NativeImage iron = NativeImage.read(ironIn);
                    NativeImage diamond = NativeImage.read(diamondIn)) {
                int w = Math.min(iron.getWidth(), diamond.getWidth());
                int h = Math.min(iron.getHeight(), diamond.getHeight());
                NativeImage out = new NativeImage(w, h, true);
                int scale = Math.max(1, w / 64);
                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        out.setPixelRGBA(x, y, isDiamondHalf(x / scale, y / scale)
                                ? diamond.getPixelRGBA(x, y) : iron.getPixelRGBA(x, y));
                    }
                }
                Minecraft.getInstance().getTextureManager().register(layer(layer), new DynamicTexture(out));
            } catch (IOException e) {
                LOGGER.error("[NetheriteSupremo] Nao foi possivel gerar a textura da armadura de fusao", e);
            }
        }
    }

    /** Metade direita de cada face = diamante; o resto = ferro. */
    private static boolean isDiamondHalf(int x, int y) {
        for (int[] f : FACES) {
            if (x >= f[0] && x < f[0] + f[2] && y >= f[1] && y < f[1] + f[3]) {
                return x - f[0] >= f[2] / 2;
            }
        }
        return false;
    }
}
