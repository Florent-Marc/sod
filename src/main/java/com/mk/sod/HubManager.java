package com.mk.sod;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class HubManager {

    public void sendToHub(Player player) {
        World hubWorld = Bukkit.getWorld("hub");
        if (hubWorld == null) {
            hubWorld = Bukkit.createWorld(new org.bukkit.WorldCreator("hub"));
        }
        Location spawn = hubWorld.getSpawnLocation();
        player.teleport(spawn);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.getInventory().clear();
    }

    public void sendAllToHub() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            sendToHub(p);
        }
    }
}
