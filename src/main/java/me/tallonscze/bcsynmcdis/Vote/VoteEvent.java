package me.tallonscze.bcsynmcdis.Vote;


import com.zaxxer.hikari.HikariDataSource;

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


    public int getVotePoint(String name){
        try (Connection connection = hikariDataSource.getConnection()){
            if(name == null){
                return 0;
            }
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT `value` FROM `voting_modpack` WHERE `minecraft_name` = ?");
            preparedStatement.setString(1, name);
            preparedStatement.execute();
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
                        .prepareStatement("INSERT INTO `voting_modpack` (`minecraft_name`, `value`) VALUES (?, ?)");
                insertStatement.setString(1, name);
                insertStatement.setInt(2, 1);
                insertStatement.execute();
            } else {
                PreparedStatement insertStatement = connection
                        .prepareStatement("UPDATE `voting_modpack` SET `value` = `value` + 1 WHERE `minecraft_name` = ?");
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
                preparedStatement.setString(1, name);
                preparedStatement.execute();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
