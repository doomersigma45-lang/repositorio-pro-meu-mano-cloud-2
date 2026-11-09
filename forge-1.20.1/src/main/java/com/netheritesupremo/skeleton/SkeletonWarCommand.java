package com.netheritesupremo.skeleton;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** /skeletonwar declare|test <pos>|arrive <pos>|mark <jogador>|unmark <jogador>|status|stop|reset (so operadores). */
public class SkeletonWarCommand {
    @SubscribeEvent
    public void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> d = event.getDispatcher();
        d.register(Commands.literal("skeletonwar").requires(s -> s.hasPermission(2))
                .then(Commands.literal("declare").executes(c -> {
                    BlockPos origin = BlockPos.containing(c.getSource().getPosition());
                    int n = SkeletonWar.declare(c.getSource().getServer(), origin, c.getSource().getTextName());
                    c.getSource().sendSuccess(() -> Component.literal("alvos: " + n), false);
                    return n;
                }))
                .then(Commands.literal("test").then(Commands.argument("pos", BlockPosArgument.blockPos()).executes(c -> {
                    BlockPos pos = BlockPosArgument.getLoadedBlockPos(c, "pos");
                    SkeletonWar.addTestSite(c.getSource().getServer().overworld(), pos);
                    c.getSource().sendSuccess(() -> Component.literal("teste iniciado em " + pos.toShortString()), false);
                    return 1;
                })))
                .then(Commands.literal("arrive").then(Commands.argument("pos", BlockPosArgument.blockPos()).executes(c -> {
                    // simula a chegada de um jogador marcado a vila/outpost em x,z (usa a altura da superficie)
                    BlockPos in = BlockPosArgument.getLoadedBlockPos(c, "pos");
                    ServerLevel ow = c.getSource().getServer().overworld();
                    BlockPos at = new BlockPos(in.getX(), ow.getHeight(Heightmap.Types.WORLD_SURFACE, in.getX(), in.getZ()) - 1, in.getZ());
                    var site = SkeletonWar.siteAt(ow, at);
                    if (site == null) {
                        c.getSource().sendSuccess(() -> Component.literal("nenhuma vila/outpost em " + at.toShortString()), false);
                        return 0;
                    }
                    boolean started = SkeletonWar.arrive(c.getSource().getServer(), site, c.getSource().getTextName());
                    c.getSource().sendSuccess(() -> Component.literal("lugar=" + site.getSecond() + " guerra=" + started), false);
                    return started ? 1 : 0;
                })))
                .then(Commands.literal("mark").then(Commands.argument("player", EntityArgument.player()).executes(c -> {
                    ServerPlayer p = EntityArgument.getPlayer(c, "player");
                    SkeletonWar.mark(p);
                    return 1;
                })))
                .then(Commands.literal("unmark").then(Commands.argument("player", EntityArgument.player()).executes(c -> {
                    SkeletonWar.unmark(EntityArgument.getPlayer(c, "player"));
                    return 1;
                })))
                .then(Commands.literal("status").executes(c -> {
                    ServerLevel ow = c.getSource().getServer().overworld();
                    SkeletonWar.Data data = SkeletonWar.Data.get(ow);
                    StringBuilder sb = new StringBuilder("sites=" + data.sites.size());
                    int i = 0;
                    for (SkeletonWar.Site s : data.sites) {
                        sb.append(" | #").append(i++).append(" kind=").append(s.kind)
                                .append(" state=").append(SkeletonWar.stateName(s.state))
                                .append(" waves=").append(s.wave).append('/').append(s.maxWaves)
                                .append(" defenders=").append(SkeletonWar.defenders(ow, s).size())
                                .append(" attackers=").append(SkeletonWar.attackers(ow, s).size());
                    }
                    String text = sb.toString();
                    c.getSource().sendSuccess(() -> Component.literal(text), false);
                    return data.sites.size();
                }))
                .then(Commands.literal("stop").executes(c -> {
                    SkeletonWar.stopAll(c.getSource().getServer().overworld(), false);
                    c.getSource().sendSuccess(() -> Component.literal("guerra encerrada"), false);
                    return 1;
                }))
                .then(Commands.literal("reset").executes(c -> {
                    SkeletonWar.stopAll(c.getSource().getServer().overworld(), true);
                    c.getSource().sendSuccess(() -> Component.literal("guerra e conquistas apagadas"), false);
                    return 1;
                })));
    }
}
