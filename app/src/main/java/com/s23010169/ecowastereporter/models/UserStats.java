package com.s23010169.ecowastereporter.models;

public class UserStats {
    private String name;
    private int level;
    private int xp;
    private int xpToNext;
    private int totalPoints;
    private int streak;
    private int rank;
    private String avatar;

    public UserStats(String name, int level, int xp, int xpToNext, int totalPoints, int streak, int rank, String avatar) {
        this.name = name;
        this.level = level;
        this.xp = xp;
        this.xpToNext = xpToNext;
        this.totalPoints = totalPoints;
        this.streak = streak;
        this.rank = rank;
        this.avatar = avatar;
    }

    // Getters
    public String getName() { return name; }
    public int getLevel() { return level; }
    public int getXp() { return xp; }
    public int getXpToNext() { return xpToNext; }
    public int getTotalPoints() { return totalPoints; }
    public int getStreak() { return streak; }
    public int getRank() { return rank; }
    public String getAvatar() { return avatar; }
} 