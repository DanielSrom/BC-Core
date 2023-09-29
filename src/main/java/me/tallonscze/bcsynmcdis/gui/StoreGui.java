package me.tallonscze.bcsynmcdis.gui;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;

public class StoreGui {

    public Inventory createInventory(ServerPlayer player){
        Inventory storeMenu = new Inventory(player);
        return null;
    }
}
