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
 * Gera a textura do golem de netherite a partir da textura do golem de ferro do proprio jogo,
 * trocando as cores para o tom escuro da netherite com brasas laranja. Assim o modelo e os UVs
 * continuam corretos sem precisar embutir arte no mod. Recriada a cada recarga de recursos.
 */
public class GolemTexture implements ResourceManagerReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final ResourceLocation LOCATION =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/netherite_golem_generated");
    private static final ResourceLocation IRON_GOLEM = new ResourceLocation("textures/entity/iron_golem/iron_golem.png");

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        NativeImage image;
        try (InputStream in = manager.getResourceOrThrow(IRON_GOLEM).open()) {
            image = NativeImage.read(in);
        } catch (IOException e) {
            LOGGER.error("[NetheriteSupremo] Nao foi possivel ler a textura do golem de ferro", e);
            return;
        }
        recolor(image);
        Minecraft.getInstance().getTextureManager().register(LOCATION, new DynamicTexture(image));
    }

    private static void recolor(NativeImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = image.getPixelRGBA(x, y); // formato ABGR
                int alpha = (pixel >>> 24) & 0xFF;
                if (alpha == 0) {
                    continue;
                }
                int r = pixel & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = (pixel >> 16) & 0xFF;
                float lum = (0.299F * r + 0.587F * g + 0.114F * b) / 255.0F;

                int nr = (int) (24 + lum * 72);
                int ng = (int) (20 + lum * 62);
                int nb = (int) (22 + lum * 64);

                // Brasas: alguns pixels claros viram laranja incandescente
                int h = hash(x / 2, y / 2);
                if (lum > 0.22F && h % 23 == 0) {
                    float glow = 0.65F + (h % 7) * 0.06F;
                    nr = Math.min(255, (int) (255 * glow));
                    ng = (int) (110 * glow);
                    nb = (int) (20 * glow);
                }
                image.setPixelRGBA(x, y, (alpha << 24) | (nb << 16) | (ng << 8) | nr);
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
