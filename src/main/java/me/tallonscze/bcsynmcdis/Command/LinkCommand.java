package me.tallonscze.bcsynmcdis.Command;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.tallonscze.bcsynmcdis.Chrom;
import me.tallonscze.bcsynmcdis.Bcsynmcdis;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Bcsynmcdis.MODID)
public class LinkCommand {

    private Chrom lchrom;

    public LinkCommand(){
        lchrom = Bcsynmcdis.chrom;
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


                            return Command.SINGLE_SUCCESS;
                        })));
        dispatcher.register(Commands.literal("getreward")
                .requires(cs -> cs.hasPermission(0))
                .executes(context -> {


                    return Command.SINGLE_SUCCESS;
                }));
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event){
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        register(dispatcher);
    }
}
