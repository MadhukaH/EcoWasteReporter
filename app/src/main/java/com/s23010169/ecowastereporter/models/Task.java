package com.s23010169.ecowastereporter.models;

public class Task {
    private String taskId;
    private String location;
    private String description;
    private String status;
    private String priority;
    private int binFullPercentage;
    private String additionalInfo;
    private double estimatedDistance;
    private int estimatedTime;
    private String formattedDistance;

    // Constructor for task selector
    public Task(String taskId, String location, String description, String status, String formattedDistance) {
        this.taskId = taskId;
        this.location = location;
        this.description = description;
        this.status = status;
        this.formattedDistance = formattedDistance;
    }

    // Constructor for existing functionality
    public Task(String location, String priority, int binFullPercentage, String additionalInfo, double estimatedDistance, int estimatedTime) {
        this.location = location;
        this.priority = priority;
        this.binFullPercentage = binFullPercentage;
        this.additionalInfo = additionalInfo;
        this.estimatedDistance = estimatedDistance;
        this.estimatedTime = estimatedTime;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public int getBinFullPercentage() {
        return binFullPercentage;
    }

    public void setBinFullPercentage(int binFullPercentage) {
        this.binFullPercentage = binFullPercentage;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public double getEstimatedDistance() {
        return estimatedDistance;
    }

    public void setEstimatedDistance(double estimatedDistance) {
        this.estimatedDistance = estimatedDistance;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getDistance() {
        return formattedDistance != null ? formattedDistance : String.format("%.1f km away", estimatedDistance);
    }

    public void setFormattedDistance(String formattedDistance) {
        this.formattedDistance = formattedDistance;
    }

    public String getFormattedDistance() {
        return String.format("%.1f km away", estimatedDistance);
    }

    public String getFormattedTime() {
        return String.format("Est. %d min", estimatedTime);
    }

    public String getBinDetails() {
        return String.format("Bin %d%% Full • %s", binFullPercentage, additionalInfo);
    }
} 