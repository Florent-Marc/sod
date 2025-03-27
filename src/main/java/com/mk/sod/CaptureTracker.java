package com.mk.sod;

import java.util.HashMap;
import java.util.Map;

public class CaptureTracker {

    private final Map<String, Map<String, Boolean>> captured = new HashMap<>();

    public void reset() {
        captured.clear();
    }

    public void setCaptured(String team, String zoneId, boolean status) {
        captured.computeIfAbsent(team.toLowerCase(), k -> new HashMap<>()).put(zoneId, status);
    }

    public boolean hasCapturedAllCapital(String team, int totalZones) {
        Map<String, Boolean> zones = captured.getOrDefault(team.toLowerCase(), new HashMap<>());
        return zones.values().stream().filter(Boolean::booleanValue).count() >= totalZones;
    }
}
