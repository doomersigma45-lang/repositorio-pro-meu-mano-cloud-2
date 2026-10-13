package com.netheritesupremo.client;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.entity.NetheriteKnight;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * Desenha o cavaleiro com a skin enviada e o modelo classico de jogador (bracos de 4 pixels).
 * Nao adiciona a camada de armadura, entao a armadura de netherite que ele veste nao aparece.
 * A espada na mao continua visivel.
 */
public class NetheriteKnightRenderer extends HumanoidMobRenderer<NetheriteKnight, PlayerModel<NetheriteKnight>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/netherite_knight.png");

    public NetheriteKnightRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(NetheriteKnight knight) {
        return TEXTURE;
    }
}
