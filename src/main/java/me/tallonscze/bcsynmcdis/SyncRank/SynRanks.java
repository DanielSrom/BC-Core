package me.tallonscze.bcsynmcdis.SyncRank;

import com.mojang.logging.LogUtils;
import dev.ftb.mods.ftbranks.api.Rank;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

import java.util.List;

public class SynRanks {

    private static final Logger LOGGER = LogUtils.getLogger();
    private MinecraftServer server;
    FtbRanks myFtbRanksAPI = new FtbRanks();
    LuckPerms myLuckAPI = new LuckPerms();

    private int tickCounter = 0;





    private void synchronizedRanks(){
        try{

            List<ServerPlayer> players = server.getPlayerList().getPlayers();
            if (players.isEmpty()){
                return;
            }
            LOGGER.info("BC-SyncRanks - Synchronization started..");
            int sizeOfPlayers = players.size();
            int helpSize = 0;

            for (ServerPlayer player : players){
                helpSize++;
                if (player == null) {
                    continue;
                }
                List<Rank> playerRanks = myFtbRanksAPI.getPlayerRanks(player);
                String rankLP = myLuckAPI.getRank(player);
                if (rankLP.equals("bronze") || rankLP.equals("osmium") || rankLP.equals("uranium")){
                    for (Rank rank : playerRanks){
                        myFtbRanksAPI.removeRankFromPlayer(player, rank);
                    }
                    myFtbRanksAPI.setRankToPlayer(player, myFtbRanksAPI.getRank(rankLP));

                } else if (playerRanks.contains(myFtbRanksAPI.getRank("senior"))){

                    for (Rank rank : playerRanks){
                        if (rank == myFtbRanksAPI.getRank("team")){
                            continue;
                        }
                        myFtbRanksAPI.removeRankFromPlayer(player, rank);
                    }
                    myLuckAPI.setRank(player, "senior");

                }  else if (playerRanks.contains(myFtbRanksAPI.getRank("mediator"))){

                    for (Rank rank : playerRanks){
                        if (rank == myFtbRanksAPI.getRank("team")){
                            continue;
                        }
                        myFtbRanksAPI.removeRankFromPlayer(player, rank);
                    }
                    myLuckAPI.setRank(player, "mediator");

                }
                else if (rankLP.equals("Player")){
                    for (Rank rank : playerRanks){
                        if (rank == myFtbRanksAPI.getRank("team")){
                            continue;
                        }
                        myFtbRanksAPI.removeRankFromPlayer(player, rank);
                    }
                    myFtbRanksAPI.setRankToPlayer(player, myFtbRanksAPI.getRank("member"));

                }
                LOGGER.info("BC-SyncRanks - Synchronization " + helpSize + "/" + sizeOfPlayers);

            }
            LOGGER.info("BC-SyncRanks - Synchronization complete..");
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.info("BC-SyncRanks - ERROR..");
        }

    }



    @SubscribeEvent
    public void onServerStarded(ServerStartingEvent event){
        server = event.getServer();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onWorldTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            tickCounter++;

            //Interval set to 1 Minutes
            int SAVE_INTERVAL = 300 * 20;
            if (tickCounter >= SAVE_INTERVAL) {
                tickCounter = 0;
                synchronizedRanks();
            }
        }
    }

}
