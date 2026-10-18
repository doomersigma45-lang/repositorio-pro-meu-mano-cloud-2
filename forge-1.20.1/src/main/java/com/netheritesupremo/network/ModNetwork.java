package com.netheritesupremo.network;

import com.netheritesupremo.CoreEvents;
import com.netheritesupremo.NetheriteSupremo;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModNetwork {
    private static final String VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(NetheriteSupremo.MODID, "main"), () -> VERSION, VERSION::equals, VERSION::equals);

    private ModNetwork() {
    }

    public static void register() {
        CHANNEL.registerMessage(0, PhasePacket.class, PhasePacket::encode, PhasePacket::decode, PhasePacket::handle);
    }

    /** Cliente -> servidor: tecla K (liga/desliga atravessar paredes). */
    public record PhasePacket(boolean enabled) {
        static void encode(PhasePacket p, FriendlyByteBuf buf) {
            buf.writeBoolean(p.enabled);
        }

        static PhasePacket decode(FriendlyByteBuf buf) {
            return new PhasePacket(buf.readBoolean());
        }

        static void handle(PhasePacket p, Supplier<NetworkEvent.Context> ctx) {
            NetworkEvent.Context context = ctx.get();
            context.enqueueWork(() -> {
                ServerPlayer player = context.getSender();
                if (player != null) {
                    player.getPersistentData().putBoolean(CoreEvents.TOGGLE, p.enabled && CoreEvents.holdsCore(player));
                }
            });
            context.setPacketHandled(true);
        }
    }
}
