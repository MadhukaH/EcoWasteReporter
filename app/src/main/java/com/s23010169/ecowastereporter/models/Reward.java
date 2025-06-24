package com.s23010169.ecowastereporter.models;

public class Reward {
    private int id;
    private String name;
    private int points;
    private String type;
    private String icon;
    private boolean claimed;

    public Reward(int id, String name, int points, String type, String icon, boolean claimed) {
        this.id = id;
        this.name = name;
        this.points = points;
        this.type = type;
        this.icon = icon;
        this.claimed = claimed;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getPoints() { return points; }
    public String getType() { return type; }
    public String getIcon() { return icon; }
    public boolean isClaimed() { return claimed; }

    // Setter for claimed status
    public void setClaimed(boolean claimed) { this.claimed = claimed; }
} 