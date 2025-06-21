package com.s23010169.ecowastereporter.models;

public class Bin {
    private String location;
    private int fillPercentage;
    private double distance;
    private double latitude;
    private double longitude;

    public Bin(String location, int fillPercentage, double distance, double latitude, double longitude) {
        this.location = location;
        this.fillPercentage = fillPercentage;
        this.distance = distance;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getFillPercentage() {
        return fillPercentage;
    }

    public void setFillPercentage(int fillPercentage) {
        this.fillPercentage = fillPercentage;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
} 