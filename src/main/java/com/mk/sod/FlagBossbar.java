package com.mk.sod;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

public class FlagBossbar {

    private final BossBar bar;

    public FlagBossbar(Main plugin) {
        this.bar = Bukkit.createBossBar("Progression", BarColor.YELLOW, BarStyle.SEGMENTED_6);
        this.bar.setVisible(true);
        Bukkit.getOnlinePlayers().forEach(bar::addPlayer);
    }

    public void updateFlags(int team1Flags, int team2Flags, int total) {
        int sum = team1Flags + team2Flags;
        double progress = (double) sum / total;
        bar.setProgress(progress);
        bar.setTitle("⚔ Anubis: " + team1Flags + "/" + total + " | Râ: " + team2Flags + "/" + total + " ⚔");
    }

    public BossBar getBar() {
        return bar;
    }
}
