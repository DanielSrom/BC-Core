package me.tallonscze.bcsynmcdis.NameFormat;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class NameFormatEvent {


    @SubscribeEvent
    public void refreshName(ServerChatEvent event){
        ServerPlayer player = event.getPlayer();
        player.refreshTabListName();
        player.refreshDisplayName();
    }

    @SubscribeEvent
    public void onChatNameFormat(PlayerEvent.NameFormat event) {
        try {
            Component playerName = event.getEntity().getName();
            String stringPlayerName = playerName.getString();
            User user = LuckPermsProvider.get().getUserManager().getUser(stringPlayerName);
            String lPrefix = " ";
            if (user != null) {
                lPrefix = user.getCachedData().getMetaData().getPrefix();
            }
            if (lPrefix != null && lPrefix.contains("&")) {
                lPrefix = lPrefix.replace("&", "§");
            }
            Component finish = Component.literal(lPrefix + " " + stringPlayerName);
            event.setDisplayname(finish);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        try {

            ServerPlayer sPlayer = (ServerPlayer) event.getEntity();

            Component header = Component.literal(" \n §4§lBurning§f§lCube Network \n ");
            Component footer = Component.literal(" \n §eDiscord §f- brcb.eu/discord\n §eWeb §f- docs.burningcube.eu \n §eStore §f- store.burningcube.eu\n");
            sPlayer.setTabListHeaderFooter(header, footer);
            updateTabName(sPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void updateTabName(ServerPlayer player){
        player.refreshTabListName();
    }

    @SubscribeEvent
    public void onPlayerTabListNameFormat(PlayerEvent.TabListNameFormat event){
        ServerPlayer player = (ServerPlayer) event.getEntity();
        event.setDisplayName(updateTabNameMetod(player));
    }

    public Component updateTabNameMetod(ServerPlayer player) {

        String name = player.getName().getString();

        User user = LuckPermsProvider.get().getUserManager().getUser(player.getUUID());
        String lPrefix = "";
        if (user != null) {
            lPrefix = user.getCachedData().getMetaData().getPrefix();
        }
        if (lPrefix != null && lPrefix.contains("&")) {
            lPrefix = lPrefix.replace("&", "§");
        }
        return Component.literal(lPrefix + " " + name);
    }

}
