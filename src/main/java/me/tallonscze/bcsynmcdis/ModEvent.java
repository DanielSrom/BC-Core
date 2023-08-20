package me.tallonscze.bcsynmcdis;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Bcsynmcdis.MODID)
public class ModEvent {
    private MinecraftServer server;
    private int tickCounter = 0;
    private final int SAVE_INTERVAL = 60 * 20;
    private Chrom lchrom;
    public ModEvent(){
        lchrom = Bcsynmcdis.chrom;
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
        System.out.println("Funguji");
        ServerPlayer player = (ServerPlayer) event.getEntity();
        String playerName = player.getName().getString();
        User user = LuckPermsProvider.get().getUserManager().getUser(playerName);
        if (server.isSingleplayer() || user == null){
            return;
        }
        if (server.getPlayerCount() >= (server.getMaxPlayers())-5){
            if(!user.getCachedData().getPermissionData().checkPermission("vipslot.primaryconnect").asBoolean()){
                Connection connec = player.connection.getConnection();
                Component disconnectReson = Component.literal("Server je plný.. Zakup si VIP pro rezervovaný slot.");
                connec.disconnect(disconnectReson);
            }
        }
        String discordName = lchrom.getPendingLink(event.getEntity().getName().toString());
        if (discordName == null){
            return;
        }
        player.sendSystemMessage(Component.literal("Discord účet" + discordName + "chce být spojeny s tvym minecraft uctem. Pokud souhlasis, pouzij prikaz /link"));

    }


    @SubscribeEvent
    public void onServerStarted(ServerStartingEvent event){
        server = event.getServer();
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onWorldTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            tickCounter++;

            if (tickCounter >= SAVE_INTERVAL) {
                tickCounter = 0;
                lchrom.publicSaveRanks(); // Call your method to save ranks here
            }
        }
    }
}


