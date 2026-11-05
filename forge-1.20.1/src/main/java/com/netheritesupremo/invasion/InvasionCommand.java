package com.netheritesupremo.invasion;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** /invasion start|test|stop|win|lose|status|reset|city|peace (so operadores). */
public class InvasionCommand {
    @SubscribeEvent
    public void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> d = event.getDispatcher();
        d.register(Commands.literal("invasion").requires(s -> s.hasPermission(2))
                .then(Commands.literal("start").executes(c -> {
                    ServerPlayer p = c.getSource().getPlayerOrException();
                    ServerLevel ow = c.getSource().getServer().overworld();
                    InvasionData data = InvasionData.get(ow);
                    InvasionManager.start(ow, data, p, p.blockPosition(), false);
                    return 1;
                }))
                .then(Commands.literal("test").then(Commands.argument("x", IntegerArgumentType.integer())
                        .then(Commands.argument("z", IntegerArgumentType.integer()).executes(c -> {
                            ServerLevel ow = c.getSource().getServer().overworld();
                            int x = IntegerArgumentType.getInteger(c, "x");
                            int z = IntegerArgumentType.getInteger(c, "z");
                            InvasionManager.start(ow, InvasionData.get(ow), null, new BlockPos(x, 64, z), true);
                            return 1;
                        }))))
                .then(Commands.literal("stop").executes(c -> {
                    ServerLevel ow = c.getSource().getServer().overworld();
                    InvasionData data = InvasionData.get(ow);
                    InvasionManager.clearInvaders(ow);
                    data.active = false;
                    data.pending = null;
                    data.setDirty();
                    return 1;
                }))
                .then(Commands.literal("win").executes(c -> {
                    ServerLevel ow = c.getSource().getServer().overworld();
                    InvasionManager.win(ow, InvasionData.get(ow));
                    return 1;
                }))
                .then(Commands.literal("lose").executes(c -> {
                    ServerLevel ow = c.getSource().getServer().overworld();
                    InvasionManager.lose(ow, InvasionData.get(ow));
                    return 1;
                }))
                .then(Commands.literal("reset").executes(c -> {
                    ServerLevel ow = c.getSource().getServer().overworld();
                    InvasionData data = InvasionData.get(ow);
                    InvasionManager.clearInvaders(ow);
                    data.active = false;
                    data.aliensWon = false;
                    data.pending = null;
                    data.cities.clear();
                    data.setDirty();
                    return 1;
                }))
                .then(Commands.literal("status").executes(c -> {
                    InvasionData data = InvasionData.get(c.getSource().getServer().overworld());
                    c.getSource().sendSuccess(() -> Component.literal("invasion: active=" + data.active + " wave=" + data.wave
                            + " bossSpawned=" + data.bossSpawned + " deaths=" + data.deaths + " villagers=" + data.villagers.size()
                            + " won=" + data.aliensWon + " cities=" + data.cities.size() + " pending=" + (data.pending != null)), false);
                    return 1;
                }))
                .then(Commands.literal("city").then(Commands.argument("x", IntegerArgumentType.integer())
                        .then(Commands.argument("z", IntegerArgumentType.integer()).executes(c -> {
                            ServerLevel ow = c.getSource().getServer().overworld();
                            boolean ok = InvasionManager.placeCity(ow, InvasionData.get(ow),
                                    new BlockPos(IntegerArgumentType.getInteger(c, "x"), 64, IntegerArgumentType.getInteger(c, "z")));
                            c.getSource().sendSuccess(() -> Component.literal("city placed=" + ok), false);
                            return ok ? 1 : 0;
                        }))))
                .then(Commands.literal("peace").then(Commands.argument("x", IntegerArgumentType.integer())
                        .then(Commands.argument("y", IntegerArgumentType.integer())
                                .then(Commands.argument("z", IntegerArgumentType.integer()).executes(c -> {
                                    ServerLevel ow = c.getSource().getServer().overworld();
                                    BlockPos p = InvasionManager.placePeaceChest(ow, new BlockPos(IntegerArgumentType.getInteger(c, "x"),
                                            IntegerArgumentType.getInteger(c, "y"), IntegerArgumentType.getInteger(c, "z")), true);
                                    c.getSource().sendSuccess(() -> Component.literal("peace chest at " + p), false);
                                    return 1;
                                }))))));
    }
}
