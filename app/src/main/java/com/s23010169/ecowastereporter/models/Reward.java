package com.s23010169.ecowastereporter.models;

// This class represents a reward that users can claim with their accumulated points.
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

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPoints(int points) { this.points = points; }
    public void setType(String type) { this.type = type; }
    public void setIcon(String icon) { this.icon = icon; }
    public void setClaimed(boolean claimed) { this.claimed = claimed; }
} 