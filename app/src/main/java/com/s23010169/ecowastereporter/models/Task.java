package com.s23010169.ecowastereporter.models;

public class Task {
    private String location;
    private String priority;
    private int binFullPercentage;
    private String additionalInfo;
    private double distance;
    private int estimatedTime;

    public Task(String location, String priority, int binFullPercentage, String additionalInfo, double distance, int estimatedTime) {
        this.location = location;
        this.priority = priority;
        this.binFullPercentage = binFullPercentage;
        this.additionalInfo = additionalInfo;
        this.distance = distance;
        this.estimatedTime = estimatedTime;
    }

    public String getLocation() {
        return location;
    }

    public String getPriority() {
        return priority;
    }

    public int getBinFullPercentage() {
        return binFullPercentage;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public double getDistance() {
        return distance;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public String getFormattedDistance() {
        return String.format("%.1f km away", distance);
    }

    public String getFormattedTime() {
        return String.format("Est. %d min", estimatedTime);
    }

    public String getBinDetails() {
        return String.format("Bin %d%% Full • %s", binFullPercentage, additionalInfo);
    }
} 