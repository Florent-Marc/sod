package com.mk.sod;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StartGameCommand implements CommandExecutor {

    public StartGameCommand(GameManager manager) {
        Bukkit.getPluginCommand("startgame").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Main.getInstance().getGameManager().startGame();
        sender.sendMessage("§aPartie lancée !");
        return true;
    }
}
