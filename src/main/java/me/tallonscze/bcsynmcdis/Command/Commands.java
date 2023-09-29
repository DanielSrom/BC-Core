package me.tallonscze.bcsynmcdis.Command;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.tallonscze.bcsynmcdis.BCCore;
import me.tallonscze.bcsynmcdis.ChromCode;
import me.tallonscze.bcsynmcdis.SyncRank.LuckPerms;
import me.tallonscze.bcsynmcdis.Vote.VoteEvent;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.swing.text.Style;

@Mod.EventBusSubscriber(modid = BCCore.MODID)
public class Commands {

    private VoteEvent voteEvent;

    private ChromCode lchrom;

    public Commands(){
        lchrom = BCCore.chrom;
        voteEvent = BCCore.voteEvent;
    }

    public void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(net.minecraft.commands.Commands.literal("link")
                .requires(cs -> cs.hasPermission(0))
                .then(net.minecraft.commands.Commands.argument("discordName", StringArgumentType.string())
                        .executes(context -> {
                            context.getSource().sendSuccess(Component.literal("Success"), true);
                            String discordNameString = StringArgumentType.getString(context, "discordName").replace("-", "#");
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            String playrName = player.getName().getString();
                            lchrom.confirm(playrName, discordNameString);
                            context.getSource().sendSuccess(Component.literal(discordNameString + " " + playrName), true);
                            return Command.SINGLE_SUCCESS;
                        })));
        dispatcher.register(net.minecraft.commands.Commands.literal("addvote")
                .requires(cs -> cs.hasPermission(4))
                .then(net.minecraft.commands.Commands.argument("playerName", StringArgumentType.word())
                        .executes(context -> {
                            String playerGameName = StringArgumentType.getString(context, "playerName");

                            voteEvent.setVotePoint(playerGameName);

                            return Command.SINGLE_SUCCESS;
                        })));

        dispatcher.register(net.minecraft.commands.Commands.literal("reward")
                //.requires(cs -> cs.hasPermission(0))
                .then(net.minecraft.commands.Commands.literal("claim")
                        .executes(context -> {
                            String playerName = context.getSource().getPlayerOrException().getName().getString();
                            int point = voteEvent.getVotePoint(playerName);
                            if (point == 0){
                                context.getSource().getPlayerOrException().sendSystemMessage(Component.literal("[§4Burning§fCube] Nemáš žádné votepointy."));
                                return Command.SINGLE_SUCCESS;
                            }
                            int help = voteEvent.giveItemToPlayer(context.getSource().getPlayerOrException(), point);
                            if (help == 1){
                                voteEvent.resetVotingPoint(playerName);
                                context.getSource().getPlayerOrException().sendSystemMessage(Component.literal("[§4Burning§fCube] Vybral jsi: " + point + " vote pointů."));
                            } else {
                                return Command.SINGLE_SUCCESS;
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(net.minecraft.commands.Commands.literal("view")
                        .executes(context -> {
                            String playerName = context.getSource().getPlayerOrException().getName().getString();
                            int point = voteEvent.getVotePoint(playerName);
                            int tPoint = voteEvent.getTotalVotePoint(playerName);
                            context.getSource().sendSystemMessage(Component.literal("[§4Burning§fCube] Právě máš: " + point + " vote pointů."));
                            context.getSource().sendSystemMessage(Component.literal("[§4Burning§fCube] Celkem máš: " + tPoint + " vote pointů."));

                            return Command.SINGLE_SUCCESS;
                        }))

                );
        dispatcher.register((net.minecraft.commands.Commands.literal("togglemessage")
                .executes(context -> {
                    ServerPlayer playerName = context.getSource().getPlayerOrException();
                    User user = LuckPerms.getUser(playerName);
                    if (!user.getCachedData().getPermissionData().checkPermission("bc.togglemessage").asBoolean()) {
                        return Command.SINGLE_SUCCESS;
                    } else if (user.getCachedData().getPermissionData().checkPermission("bc.automassage").asBoolean()){
                        user.data().remove(Node.builder("bc.automassage").build());
                        context.getSource().sendSystemMessage(Component.literal("[§4Burning§fCube] Zapnul jsi si AutoMessage."));
                        LuckPermsProvider.get().getUserManager().saveUser(user);
                    }else{
                        user.data().add(Node.builder("bc.automassage").build());
                        context.getSource().sendSystemMessage(Component.literal("[§4Burning§fCube] Vypnul jsi si AutoMessage."));
                        LuckPermsProvider.get().getUserManager().saveUser(user);
                    }
                    return Command.SINGLE_SUCCESS;
                })));
        /*
        dispatcher.register((net.minecraft.commands.Commands.literal("store")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();

                    return Command.SINGLE_SUCCESS;
        })));

         */
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event){
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        register(dispatcher);
    }
}
