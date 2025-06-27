package com.s23010169.ecowastereporter.models;

public class Level {
    private int levelNumber;
    private String title;
    private String description;
    private String icon;
    private int requiredPoints;
    private boolean isUnlocked;
    private boolean isCurrentLevel;

    public Level(int levelNumber, String title, String description, String icon, int requiredPoints, boolean isUnlocked, boolean isCurrentLevel) {
        this.levelNumber = levelNumber;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.requiredPoints = requiredPoints;
        this.isUnlocked = isUnlocked;
        this.isCurrentLevel = isCurrentLevel;
    }

    // Getters
    public int getLevelNumber() { return levelNumber; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getIcon() { return icon; }
    public int getRequiredPoints() { return requiredPoints; }
    public boolean isUnlocked() { return isUnlocked; }
    public boolean isCurrentLevel() { return isCurrentLevel; }

    // Setters
    public void setLevelNumber(int levelNumber) { this.levelNumber = levelNumber; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setIcon(String icon) { this.icon = icon; }
    public void setRequiredPoints(int requiredPoints) { this.requiredPoints = requiredPoints; }
    public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }
    public void setCurrentLevel(boolean currentLevel) { isCurrentLevel = currentLevel; }
} 