package com.s23010169.ecowastereporter.models;

import android.net.Uri;
import java.util.List;

// This class holds one waste report.
// It stores what type of waste, where it is, optional description,
// photos, time, map location, status, and a short ID.
public class Report {
    private String wasteType;
    private String location;
    private String description;
    private double latitude;
    private double longitude;
    private List<Uri> photoUris;
    private long timestamp;
    private String status = "Pending"; // Default status
    private String reportId; // Unique identifier for the report

    // Default constructor
    public Report() {
    }

    // Getters and Setters
    public String getWasteType() {
        return wasteType;
    }

    public void setWasteType(String wasteType) {
        this.wasteType = wasteType;
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

    public List<Uri> getPhotoUris() {
        return photoUris;
    }

    public void setPhotoUris(List<Uri> photoUris) {
        this.photoUris = photoUris;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }
} 