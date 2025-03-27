package com.mk.sod;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {

    private static Main instance;
    private GameManager gameManager;
    private PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        if (!checkEnvironment()) {
            log("❌ Erreurs détectées au démarrage. Plugin désactivé.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        gameManager = new GameManager();
        playerDataManager = new PlayerDataManager();
        new TeamSelectorListener(gameManager, this);
        new StartGameCommand(gameManager);

        log("✅ Plugin SandsOfDominion activé avec succès.");
    }

    private boolean checkEnvironment() {
        boolean ok = true;

        File backupMap = new File(getDataFolder(), "map-backup/vallee");
        if (!backupMap.exists() || !backupMap.isDirectory()) {
            log("⚠️ Le dossier de sauvegarde map-backup/vallee est manquant ou incorrect.");
            ok = false;
        }

        File mapConfig = new File(getDataFolder(), "maps/vallee.yml");
        if (!mapConfig.exists()) {
            log("⚠️ Le fichier de configuration de la map maps/vallee.yml est introuvable.");
            ok = false;
        }

        File hub = new File(Bukkit.getWorldContainer(), "hub");
        if (!hub.exists()) {
            log("⚠️ Le monde 'hub' est manquant. Il sera généré automatiquement, mais vous pouvez aussi le créer manuellement.");
        }

        return ok;
    }

    @Override
    public void onDisable() {
        log("Plugin SandsOfDominion désactivé.");
    }

    public static Main getInstance() {
        return instance;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public static void log(String message) {
        Bukkit.getLogger().info("[SandsOfDominion] " + message);
    }
}
