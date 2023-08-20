package me.tallonscze.bcsynmcdis.Command;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.tallonscze.bcsynmcdis.Bcsynmcdis;
import me.tallonscze.bcsynmcdis.Chrom;
import me.tallonscze.bcsynmcdis.Vote.VoteEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Bcsynmcdis.MODID)
public class LinkCommand {

    private VoteEvent voteEvent;

    private Chrom lchrom;

    public LinkCommand(){
        lchrom = Bcsynmcdis.chrom;
        voteEvent = Bcsynmcdis.voteEvent;
    }

    public void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("link")
                .requires(cs -> cs.hasPermission(0))
                .then(Commands.argument("discordName", StringArgumentType.string())
                        .executes(context -> {
                            context.getSource().sendSuccess(Component.literal("Success"), true);
                            String discordNameString = StringArgumentType.getString(context, "discordName").replace("-", "#");
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            String playrName = player.getName().getString();
                            lchrom.confirm(playrName, discordNameString);
                            context.getSource().sendSuccess(Component.literal(discordNameString + " " + playrName), true);
                            return Command.SINGLE_SUCCESS;
                        })));
        dispatcher.register(Commands.literal("addvote")
                .requires(cs -> cs.hasPermission(4))
                .then(Commands.argument("playerName", StringArgumentType.word())
                        .executes(context -> {
                            String playerGameName = StringArgumentType.getString(context, "playerName");

                            voteEvent.setVotePoint(playerGameName);

                            return Command.SINGLE_SUCCESS;
                        })));

        dispatcher.register(Commands.literal("reward")
                //.requires(cs -> cs.hasPermission(0))
                .then(Commands.literal("claim")
                        .executes(context -> {
                            String playerName = context.getSource().getPlayerOrException().getName().getString();
                            int point = voteEvent.getVotePoint(playerName);
                            if (point == 0){
                                context.getSource().getPlayerOrException().sendSystemMessage(Component.literal("[BurningCube] Nemáš žádné votepointy."));
                                return Command.SINGLE_SUCCESS;
                            }
                            voteEvent.resetVotingPoint(playerName);
                            context.getSource().getPlayerOrException().sendSystemMessage(Component.literal("Vybral jsi: " + point + " vote pointů."));
                            voteEvent.giveItemToPlayer(context.getSource().getPlayerOrException(), point);
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("get")
                        .executes(context -> {
                            String playerName = context.getSource().getPlayerOrException().getName().getString();
                            int point = voteEvent.getVotePoint(playerName);
                            context.getSource().sendSystemMessage(Component.literal("Právě máš: " + point + " vote pointů."));

                            return Command.SINGLE_SUCCESS;
                        }))

                );

    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event){
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        register(dispatcher);
    }
}
