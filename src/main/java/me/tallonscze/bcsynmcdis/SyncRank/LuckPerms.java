package me.tallonscze.bcsynmcdis.SyncRank;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.minecraft.server.level.ServerPlayer;

public class LuckPerms {
    public static User getUser(ServerPlayer player){
        return LuckPermsProvider.get().getUserManager().getUser(player.getUUID());
    }

    public String getRank(ServerPlayer player){
        User user = getUser(player);
        return user.getPrimaryGroup();
    }

    public void setRank(ServerPlayer player, String rank){
        User user = getUser(player);
        user.data().add(Node.builder("group."+rank).build());
        LuckPermsProvider.get().getUserManager().saveUser(user);
    }
}
