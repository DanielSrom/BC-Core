package me.tallonscze.bcsynmcdis.Vote;


import com.zaxxer.hikari.HikariDataSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class VoteEvent {

    private final HikariDataSource hikariDataSource;
    public VoteEvent(String host, int port, String database, String username, String password) {


        String finalUrl = "jdbc:mariadb://" + host + ":" + port + "/" + database;
        hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(finalUrl);
        hikariDataSource.setUsername(username);
        hikariDataSource.setPassword(password);
        hikariDataSource.setMaximumPoolSize(10);
        hikariDataSource.setMinimumIdle(10);
        hikariDataSource.setMaxLifetime(1800000);
        hikariDataSource.setConnectionTimeout(5000);
        hikariDataSource.setLeakDetectionThreshold(30000);

    }

    public int getTotalVotePoint(String name){
        try (Connection connection = hikariDataSource.getConnection()){
            if(name == null){
                return 0;
            }
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT `total_value` FROM `voting_modpack` WHERE `minecraft_name` = ?");
            preparedStatement.setString(1, name);
            preparedStatement.execute();
            preparedStatement.getResultSet().next();
            return preparedStatement.getResultSet().getInt("total_value");
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public int getVotePoint(String name){
        try (Connection connection = hikariDataSource.getConnection()){
            if(name == null){
                return 0;
            }
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT `value` FROM `voting_modpack` WHERE `minecraft_name` = ?");
            preparedStatement.setString(1, name);
            preparedStatement.execute();
            preparedStatement.getResultSet().next();
            return preparedStatement.getResultSet().getInt("value");
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public void setVotePoint(String name){
        try (Connection connection = hikariDataSource.getConnection()){
            if(name == null){
                return;
            }
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT COUNT(*) FROM `voting_modpack` WHERE `minecraft_name` = ?");
            preparedStatement.setString(1, name);
            preparedStatement.execute();
            preparedStatement.getResultSet().next();
            int rowCount = preparedStatement.getResultSet().getInt(1);

            if (rowCount == 0){
                PreparedStatement insertStatement = connection
                        .prepareStatement("INSERT INTO `voting_modpack` (`minecraft_name`, `value`, `total_value`) VALUES (?, ?, ?)");
                insertStatement.setString(1, name);
                insertStatement.setInt(2, 1);
                insertStatement.setInt(3, 1);
                insertStatement.execute();
            } else {
                PreparedStatement insertStatement = connection
                        .prepareStatement("UPDATE `voting_modpack` SET `value` = `value` + 1, `total_value` = `total_value` + 1 WHERE `minecraft_name` = ?");
                insertStatement.setString(1, name);
                insertStatement.execute();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void resetVotingPoint(String name){
        try (Connection connection = hikariDataSource.getConnection()){
            if(name == null){
                return;
            }
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT COUNT(*) FROM `voting_modpack` WHERE `minecraft_name` = ?");
            preparedStatement.setString(1, name);
            preparedStatement.execute();
            preparedStatement.getResultSet().next();
            int rowCount = preparedStatement.getResultSet().getInt(1);

            if (rowCount == 0){
                return;
            } else {
                PreparedStatement insertStatement = connection
                        .prepareStatement("UPDATE `voting_modpack` SET `value` = 0 WHERE `minecraft_name` = ?");
                insertStatement.setString(1, name);
                insertStatement.execute();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int giveItemToPlayer(Player player, int point){
        Item burningCubeCoin = getBurningCubeCoin();
        if (burningCubeCoin == null){
            return 2;
        }
        ItemStack itemStack = new ItemStack(burningCubeCoin, point);
        Inventory inventory = player.getInventory();

        if (!inventory.add(itemStack.copy())){
            player.drop(itemStack, false);
            return 1;
        }
        return 1;
    }

    public Item getBurningCubeCoin(){
        if (ModList.get().isLoaded("kubejs")){
            try{
                ResourceLocation itemLocation = new ResourceLocation("kubejs:burningcube_coin");
                Item coin = ForgeRegistries.ITEMS.getValue(itemLocation);
                return coin;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            return null;
        }
        return null;
    }


}
