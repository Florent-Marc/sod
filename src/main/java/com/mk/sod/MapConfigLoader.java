package com.mk.sod;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapConfigLoader {

    private String mapName;
    private Location teamSpawnA;
    private Location teamSpawnB;
    private final Map<String, CapitalData> capitals = new HashMap<>();

    public static class CapitalData {
        public final String team;
        public final List<Location> flags = new ArrayList<>();

        public CapitalData(String team) {
            this.team = team;
        }
    }

    public void load(File configFile) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        mapName = config.getString("map-name", "inconnue");

        String world = config.getString("world", "world");

        teamSpawnA = parseLocation(config.getString("team-spawn-a"), world);
        teamSpawnB = parseLocation(config.getString("team-spawn-b"), world);

        if (config.contains("capitals")) {
            for (String team : config.getConfigurationSection("capitals").getKeys(false)) {
                CapitalData data = new CapitalData(team);
                List<String> flags = config.getStringList("capitals." + team + ".flags");
                for (String s : flags) {
                    Location flagLoc = parseLocation(s, world);
                    if (flagLoc != null) {
                        data.flags.add(flagLoc);
                    }
                }
                capitals.put(team.toLowerCase(), data);
            }
        }
    }

    private Location parseLocation(String locStr, String worldName) {
        if (locStr == null || locStr.isEmpty()) return null;
        String[] parts = locStr.split(",");
        if (parts.length < 3) return null;
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            Main.log("⚠️ Monde introuvable: " + worldName);
            return null;
        }
        try {
            double x = Double.parseDouble(parts[0]);
            double y = Double.parseDouble(parts[1]);
            double z = Double.parseDouble(parts[2]);
            return new Location(world, x, y, z);
        } catch (NumberFormatException e) {
            Main.log("❌ Erreur parsing location: " + locStr);
            return null;
        }
    }

    public String getMapName() {
        return mapName;
    }

    public Location getTeamSpawnA() {
        return teamSpawnA;
    }

    public Location getTeamSpawnB() {
        return teamSpawnB;
    }

    public CapitalData getCapital(String team) {
        return capitals.get(team.toLowerCase());
    }
}
