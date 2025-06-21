package com.s23010169.ecowastereporter.models;

public class Report {
    private String title;
    private String location;
    private String reportId;
    private String timeAgo;
    private String status;

    public Report(String title, String location, String reportId, String timeAgo, String status) {
        this.title = title;
        this.location = location;
        this.reportId = reportId;
        this.timeAgo = timeAgo;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getReportId() {
        return reportId;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public String getStatus() {
        return status;
    }
} 