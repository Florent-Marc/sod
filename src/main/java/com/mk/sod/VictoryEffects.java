package com.mk.sod;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class VictoryEffects {

    public static void play(String team) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (team.equalsIgnoreCase(Main.getInstance().getGameManager().getAssignedTeam(player))) {
                player.sendTitle("§6VICTOIRE !", "§eVotre équipe a remporté la partie", 10, 60, 10);
                player.getWorld().spawnParticle(Particle.FIREWORK, player.getLocation(), 100, 1, 1, 1);
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
            } else {
                player.sendTitle("§cDéfaite", "§7L'équipe ennemie a capturé la capitale", 10, 60, 10);
            }
        }
    }
}
