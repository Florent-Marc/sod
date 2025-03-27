package com.mk.sod;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class GameManager {

    private boolean gameRunning = false;
    private long lastGameEnd = 0L;
    private final MapConfigLoader mapConfig;
    private final CaptureTracker tracker;
    private final FlagBossbar bossbar;
    private final HubManager hubManager;
    private final Map<Player, String> playerTeams;

    public GameManager() {
        this.mapConfig = new MapConfigLoader();
        this.tracker = new CaptureTracker();
        this.bossbar = new FlagBossbar(Main.getInstance());
        this.hubManager = new HubManager();
        this.playerTeams = new HashMap<>();
    }

    public void startGame() {
        if (gameRunning) return;

        gameRunning = true;

        // ✅ Charger automatiquement si absent
        if (Bukkit.getWorld("vallee") == null) {
            Bukkit.createWorld(new WorldCreator("vallee"));
            Main.log("🌍 Monde 'vallee' chargé automatiquement.");
        }

        File configFile = new File(Main.getInstance().getDataFolder(), "maps/vallee.yml");
        mapConfig.load(configFile);

        tracker.reset();

        for (Player player : Bukkit.getOnlinePlayers()) {
            String team = playerTeams.get(player);
            if (team == null) {
                hubManager.sendToHub(player);
                continue;
            }
            if (team.equals("anubis")) {
                player.teleport(mapConfig.getTeamSpawnA());
            } else {
                player.teleport(mapConfig.getTeamSpawnB());
            }
        }

        Bukkit.broadcastMessage("§eLa partie commence sur la map " + mapConfig.getMapName() + " ! Capturez les villages !");

        for (String team : new String[]{"anubis", "ra"}) {
            MapConfigLoader.CapitalData capital = mapConfig.getCapital(team);
            if (capital != null && !capital.flags.isEmpty()) {
                int index = 1;
                for (Location flagLoc : capital.flags) {
                    String zoneId = "capital_" + team + "_" + index;
                    new CaptureManager(flagLoc, 5, 10, team, zoneId, false, true, tracker, this);
                    index++;
                }
            }
        }

        bossbar.updateFlags(0, 0, 2);
    }


    public void endGame() {
        gameRunning = false;
        lastGameEnd = System.currentTimeMillis() / 1000L;
        Bukkit.broadcastMessage("§cFin de la partie. GG ! Stats en sauvegarde...");

        Main.getInstance().getPlayerDataManager().saveAll();

        hubManager.sendAllToHub();

        resetGameWorld("vallee");
    }

    public void resetGameWorld(String worldName) {
        Main.log("Reset du monde: " + worldName);

        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            Bukkit.unloadWorld(world, false);
        }

        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        if (worldFolder.exists()) {
            try {
                deleteDirectory(worldFolder.toPath());
                Main.log("Dossier monde supprimé.");
            } catch (IOException e) {
                Main.log("Erreur lors de la suppression: " + e.getMessage());
                return;
            }
        }

        File backup = new File(Main.getInstance().getDataFolder(), "map-backup/" + worldName);
        if (!backup.exists()) {
            Main.log("Backup du monde introuvable: " + backup.getAbsolutePath());
            return;
        }

        try {
            copyDirectory(backup.toPath(), worldFolder.toPath());
            Main.log("Monde restauré depuis backup.");
        } catch (IOException e) {
            Main.log("Erreur lors de la copie: " + e.getMessage());
            return;
        }

        Bukkit.createWorld(new WorldCreator(worldName));
        Main.log("Monde rechargé avec succès.");
    }

    private void deleteDirectory(Path path) throws IOException {
        if (Files.notExists(path)) return;
        Files.walk(path)
                .sorted((a, b) -> b.compareTo(a))
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        Main.log("Erreur suppression fichier: " + p);
                    }
                });
    }

    private void copyDirectory(Path source, Path target) throws IOException {
        Files.walk(source).forEach(s -> {
            Path d = target.resolve(source.relativize(s));
            try {
                if (Files.isDirectory(s)) {
                    if (!Files.exists(d)) Files.createDirectory(d);
                } else {
                    Files.copy(s, d, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                Main.log("Erreur copie fichier: " + s);
            }
        });
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    public long timeSinceLastGame() {
        return (System.currentTimeMillis() / 1000L) - lastGameEnd;
    }

    public void checkVictoryCondition(String team) {
        if (tracker.hasCapturedAllCapital(team, 2)) {
            VictoryEffects.play(team);
            Bukkit.broadcastMessage("✨ §6VICTOIRE ! " + getTeamDisplayName(team) + " remporte la partie ! ✨");
            endGame();
        }
    }

    public CaptureTracker getTracker() {
        return tracker;
    }

    public FlagBossbar getBossbar() {
        return bossbar;
    }

    public HubManager getHubManager() {
        return hubManager;
    }

    public void assignPlayerToTeam(Player player, String team) {
        playerTeams.put(player, team.toLowerCase());
    }

    public String getAssignedTeam(Player player) {
        return playerTeams.get(player);
    }

    public MapConfigLoader getMap() {
        return mapConfig;
    }

    public static String getTeamDisplayName(String teamId) {
        return switch (teamId.toLowerCase()) {
            case "anubis" -> "Légion d'Anubis";
            case "ra" -> "Soleil de Râ";
            default -> teamId;
        };
    }
}
