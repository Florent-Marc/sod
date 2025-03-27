package com.mk.sod;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;

public class TeamSelectorListener implements Listener {

    private final GameManager gameManager;

    public TeamSelectorListener(GameManager manager, JavaPlugin plugin) {
        this.gameManager = manager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || event.getClickedBlock() == null) return;

        if (event.getClickedBlock().getType().toString().contains("SIGN")) {
            Sign sign = (Sign) event.getClickedBlock().getState();
            String header = ChatColor.stripColor(sign.getLine(0));

            if ("[Team]".equalsIgnoreCase(header)) {
                String team = ChatColor.stripColor(sign.getLine(1)).toLowerCase();

                if (!team.equals("anubis") && !team.equals("ra")) return;

                Player player = event.getPlayer();
                if (gameManager.getAssignedTeam(player) != null) {
                    player.sendMessage("§cVous avez déjà rejoint une équipe.");
                    return;
                }

                gameManager.assignPlayerToTeam(player, team);
                player.sendMessage("§aVous avez rejoint l'équipe: " + GameManager.getTeamDisplayName(team));

                // ✅ Téléportation immédiate dès le choix de l’équipe
                Location spawn = "anubis".equals(team)
                        ? gameManager.getMap().getTeamSpawnA()
                        : gameManager.getMap().getTeamSpawnB();

                if (spawn != null) {
                    player.teleport(spawn);
                } else {
                    player.sendMessage("§cLe point de spawn de cette équipe n'est pas encore défini.");
                }
            }
        }
    }
}
