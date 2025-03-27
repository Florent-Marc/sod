package com.mk.sod;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class PlayerDataManager {

    private final Map<Player, PlayerStats> playerStatsMap = new HashMap<>();
    private Connection connection;

    public PlayerDataManager() {
        File dbFile = new File(Main.getInstance().getDataFolder(), "playerdata.db");
        try {
            if (!dbFile.exists()) {
                dbFile.getParentFile().mkdirs();
                dbFile.createNewFile();
            }

            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS stats (" +
                                "uuid TEXT PRIMARY KEY," +
                                "captures INTEGER DEFAULT 0," +
                                "wins INTEGER DEFAULT 0" +
                                ")");
            }

        } catch (Exception e) {
            Main.log("Erreur connexion base de données: " + e.getMessage());
        }
    }

    public PlayerStats getStats(Player player) {
        return playerStatsMap.computeIfAbsent(player, p -> {
            try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM stats WHERE uuid = ?")) {
                stmt.setString(1, p.getUniqueId().toString());
                ResultSet rs = stmt.executeQuery();
                PlayerStats stats = new PlayerStats();
                if (rs.next()) {
                    stats.setCaptures(rs.getInt("captures"));
                    stats.setWins(rs.getInt("wins"));
                }
                return stats;
            } catch (SQLException e) {
                Main.log("Erreur chargement stats: " + e.getMessage());
                return new PlayerStats();
            }
        });
    }

    public void saveAll() {
        for (Map.Entry<Player, PlayerStats> entry : playerStatsMap.entrySet()) {
            Player player = entry.getKey();
            PlayerStats stats = entry.getValue();
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO stats (uuid, captures, wins) VALUES (?, ?, ?) " +
                            "ON CONFLICT(uuid) DO UPDATE SET captures = excluded.captures, wins = excluded.wins")) {
                stmt.setString(1, player.getUniqueId().toString());
                stmt.setInt(2, stats.getCaptures());
                stmt.setInt(3, stats.getWins());
                stmt.executeUpdate();
            } catch (SQLException e) {
                Main.log("Erreur sauvegarde stats: " + e.getMessage());
            }
        }
    }
}
