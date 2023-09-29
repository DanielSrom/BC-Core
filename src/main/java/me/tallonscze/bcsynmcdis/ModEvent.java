package me.tallonscze.bcsynmcdis;

import me.tallonscze.bcsynmcdis.SyncRank.LuckPerms;
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

@Mod.EventBusSubscriber(modid = BCCore.MODID)
public class ModEvent {
    private MinecraftServer server;
    private int tickCounter = 0;
    private int autoMassage = 0;
    private final int SAVE_INTERVAL = 600 * 20;
    private ChromCode lchrom;
    public ModEvent(){
        lchrom = BCCore.chrom;
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
        if (server.getPlayerCount() >= (server.getMaxPlayers())-4){
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
        autoMessage();
    }

    public void autoMessage(){

        if(autoMassage == 0){
            server.getPlayerList().getPlayers().forEach(player -> {
                User user = LuckPerms.getUser(player);
                if(!user.getCachedData().getPermissionData().checkPermission("bc.automassage").asBoolean()){
                    player.sendSystemMessage(Component.literal("[§4Burning§fCube] §eHlasuj pro náš server a získej odměny. Hlasovat můžeš díky §6/vote§e, odměny získáš díky §6/reward claim"));
                }
            });
        } else if(autoMassage==1){
            server.getPlayerList().getPlayers().forEach(player -> {
                User user = LuckPerms.getUser(player);
                if(!user.getCachedData().getPermissionData().checkPermission("bc.automassage").asBoolean()){
                    player.sendSystemMessage(Component.literal("[§4Burning§fCube] §eBalíčky plné výhod si můžeš zakoupit na §6https://store.burningcube.eu"));
                }
            });
        } else if (autoMassage == 2){
            server.getPlayerList().getPlayers().forEach(player -> {
                User user = LuckPerms.getUser(player);
                if(!user.getCachedData().getPermissionData().checkPermission("bc.automassage").asBoolean()){
                    player.sendSystemMessage(Component.literal("[§4Burning§fCube] §eK zobrazení počtu VotePointů slouží §6/reward §eview. BurnincCube coiny můžeš využít k obchodování v Questech."));
                }
            });
        } else if (autoMassage == 3){
            server.getPlayerList().getPlayers().forEach(player -> {
                User user = LuckPerms.getUser(player);
                if(!user.getCachedData().getPermissionData().checkPermission("bc.automassage").asBoolean()){
                    player.sendSystemMessage(Component.literal("[§4Burning§fCube] §eBurnincCube coiny můžeš využít k obchodování v Questech."));
                }
            });
        }
        autoMassage++;
        if(autoMassage==3){
            autoMassage=0;
        }
        System.out.println("[BurningCube] Právě byla odeslána zpráva hráčům...");
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onWorldTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            tickCounter++;

            if (tickCounter >= SAVE_INTERVAL) {
                tickCounter = 0;
                lchrom.publicSaveRanks(); // Call your method to save ranks here
                autoMessage();
            }
        }
    }
}


