package com.netheritesupremo.blackrealm;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** /blackrealm enter|leave (so operadores): entra ou sai da Terra Negra. */
public class BlackRealmCommand {
    @SubscribeEvent
    public void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> d = event.getDispatcher();
        d.register(Commands.literal("blackrealm").requires(s -> s.hasPermission(2))
                .then(Commands.literal("enter").executes(c -> {
                    ServerPlayer p = c.getSource().getPlayerOrException();
                    boolean ok = BlackRealm.banish(p, p.serverLevel());
                    c.getSource().sendSuccess(() -> Component.literal(ok ? "Terra Negra" : "Nao foi possivel entrar"), false);
                    return ok ? 1 : 0;
                }))
                .then(Commands.literal("leave").executes(c -> {
                    ServerPlayer p = c.getSource().getPlayerOrException();
                    boolean ok = BlackRealm.leave(p);
                    c.getSource().sendSuccess(() -> Component.literal(ok ? "Overworld" : "Nao foi possivel sair"), false);
                    return ok ? 1 : 0;
                })));
    }
}
