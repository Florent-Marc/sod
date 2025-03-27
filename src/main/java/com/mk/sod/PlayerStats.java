package com.mk.sod;

public class PlayerStats {

    private int captures;
    private int wins;

    public int getCaptures() {
        return captures;
    }

    public void setCaptures(int captures) {
        this.captures = captures;
    }

    public void addCapture() {
        this.captures++;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void addWin() {
        this.wins++;
    }
}
