package com.mk.sod;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class CaptureManager {

    private final Location center;
    private final double radius;
    private final int captureTime;
    private final String team;
    private final String zoneId;
    private final boolean neutralZone;
    private final boolean capitalZone;
    private final CaptureTracker tracker;
    private final GameManager gameManager;

    private final Set<Player> inside = new HashSet<>();
    private int ticksInside = 0;

    public CaptureManager(Location center, double radius, int captureTime, String team, String zoneId,
                          boolean neutralZone, boolean capitalZone, CaptureTracker tracker, GameManager manager) {
        this.center = center;
        this.radius = radius;
        this.captureTime = captureTime;
        this.team = team;
        this.zoneId = zoneId;
        this.neutralZone = neutralZone;
        this.capitalZone = capitalZone;
        this.tracker = tracker;
        this.gameManager = manager;

        runTask();
    }

    private void runTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!gameManager.isGameRunning()) {
                    cancel();
                    return;
                }

                inside.clear();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getLocation().getWorld().equals(center.getWorld())
                            && player.getLocation().distance(center) <= radius) {
                        inside.add(player);
                    }
                }

                boolean enemyPresent = inside.stream()
                        .anyMatch(p -> {
                            String teamP = gameManager.getAssignedTeam(p);
                            return teamP != null && !teamP.equalsIgnoreCase(team);
                        });

                if (!enemyPresent) {
                    ticksInside = 0;
                    return;
                }

                ticksInside++;
                center.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, center, 5, 1, 1, 1, 0.1);

                if (ticksInside >= captureTime * 20) {
                    tracker.setCaptured(team, zoneId, true);
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Une zone a été capturée !");
                    if (capitalZone) {
                        gameManager.checkVictoryCondition(team);
                    }
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L);
    }
}
