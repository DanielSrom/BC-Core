package me.tallonscze.bcsynmcdis.SyncRank;

import com.mojang.authlib.GameProfile;
import dev.ftb.mods.ftbranks.api.FTBRanksAPI;
import dev.ftb.mods.ftbranks.api.Rank;
import dev.ftb.mods.ftbranks.api.RankManager;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class FtbRanks {


    public RankManager getRankManager(){
        try{
            return FTBRanksAPI.INSTANCE.getManager();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error in RankManager..");
        }
        return null;
    }

    public Rank getRank(String rank){
        Rank rank1 = getRankManager().getRank(rank).orElse(null);
        return rank1;
    }

    public List<Rank> getPlayerRanks(ServerPlayer player){
        return getRankManager().getRanks(player);
    }

    public void setRankToPlayer(ServerPlayer player, Rank rank){
        rank.add(getGameProfile(player));
    }

    public void removeRankFromPlayer(ServerPlayer player, Rank rank){
        rank.remove(getGameProfile(player));
    }

    private GameProfile getGameProfile(ServerPlayer player){
        return player.getGameProfile();
    }

}
