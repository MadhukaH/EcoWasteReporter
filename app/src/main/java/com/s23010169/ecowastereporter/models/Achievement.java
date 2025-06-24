package com.s23010169.ecowastereporter.models;

public class Achievement {
    private String name;
    private String description;
    private boolean unlocked;
    private String rarity;

    public Achievement(String name, String description, boolean unlocked, String rarity) {
        this.name = name;
        this.description = description;
        this.unlocked = unlocked;
        this.rarity = rarity;
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isUnlocked() { return unlocked; }
    public String getRarity() { return rarity; }

    // Get color based on rarity
    public int getColorRes() {
        switch (rarity.toLowerCase()) {
            case "common":
                return android.R.color.darker_gray;
            case "rare":
                return android.R.color.holo_blue_dark;
            case "epic":
                return android.R.color.holo_purple;
            case "legendary":
                return android.R.color.holo_orange_dark;
            default:
                return android.R.color.darker_gray;
        }
    }
} 