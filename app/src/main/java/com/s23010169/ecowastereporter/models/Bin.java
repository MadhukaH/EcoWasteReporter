package com.s23010169.ecowastereporter.models;

// This class represents a nearby trash bin shown in lists and on the map.
public class Bin {
    private int id;
    private String location;
    private int fillPercentage;
    private double distance;
    private double latitude;
    private double longitude;
    private String status;

    public Bin(int id, String location, int fillPercentage, double distance, double latitude, double longitude, String status) {
        this.id = id;
        this.location = location;
        this.fillPercentage = fillPercentage;
        this.distance = distance;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

    public Bin(String location, int fillPercentage, double distance, double latitude, double longitude) {
        this.location = location;
        this.fillPercentage = fillPercentage;
        this.distance = distance;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = "Empty";
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 