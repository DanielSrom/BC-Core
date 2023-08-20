package me.tallonscze.bcsynmcdis;

import com.zaxxer.hikari.HikariDataSource;
import net.luckperms.api.LuckPermsProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.Set;

public class Chrom {
    private final HikariDataSource hikariDataSource;
    public Chrom(String host, int port, String database, String username, String password) {


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

    private void saveRanks() {
        Set<String> verified = getAllVerified();
        for (String name : verified) {
            LuckPermsProvider.get().getUserManager().lookupUniqueId(name)
                    .thenAcceptAsync(uuid -> {
                        if (uuid == null) {
                            setRank(name, null);
                            return;
                        }
                        LuckPermsProvider.get().getUserManager()
                                .loadUser(uuid)
                                .thenAccept(user -> {
                                    if (user == null) {
                                        setRank(name, null);
                                        return;
                                    }
                                    String apiRank = user.getPrimaryGroup();
                                    setRank(name, apiRank);
                                });
                    });
        }
    }
    public String getPendingLink(String name) {
        try (Connection connection = hikariDataSource.getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM `linked_accounts` WHERE `minecraft_name` = ? AND `verified` = 0");
            preparedStatement.setString(1, name);
            preparedStatement.execute();
            if (preparedStatement.getResultSet().next()) {
                return preparedStatement.getResultSet().getString("discord_username");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public boolean confirm(String name, String discordName) {
        if (getPendingLink(name) == null)
            return false;
        if (name == null || discordName == null)
            return false;
        if (!discordName.equals(getPendingLink(name)))
            return false;
        try (Connection connection = hikariDataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE `linked_accounts` SET `verified` = 1 WHERE `minecraft_name` = ? AND `discord_username` = ?");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, discordName);
            preparedStatement.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void setRank(String mcName, String rank) {
        try (Connection connection = hikariDataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE `linked_accounts` SET `modpack_rank` = ? WHERE `minecraft_name` = ?");
            preparedStatement.setString(1, rank);
            preparedStatement.setString(2, mcName);
            preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Set<String> getAllVerified() {
        try (Connection connection = hikariDataSource.getConnection()) {
            Set<String> verified = new HashSet<>();
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM `linked_accounts` WHERE `verified` = 1");
            preparedStatement.execute();
            while (preparedStatement.getResultSet().next()) {
                verified.add(preparedStatement.getResultSet().getString("minecraft_name"));
            }
            preparedStatement.close();
            return verified;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    public void publicSaveRanks(){
        saveRanks();
    }
}