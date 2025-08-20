package com.s23010169.ecowastereporter.models;

// This class represents a waste collection worker with personal details, work area, and performance metrics.
public class Cleaner {
    private int id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String area;
    private String experience;
    private String registrationDate;
    private String status; // active, inactive
    private int tasksCompleted;
    private float rating;
    private String profilePhoto;

    // Default constructor
    public Cleaner() {
    }

    // Constructor without id (for new records)
    public Cleaner(String name, String email, String password, String phone, String area, String experience) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.area = area;
        this.experience = experience;
        this.status = "active";
        this.tasksCompleted = 0;
        this.rating = 0.0f;
    }

    // Constructor with all fields
    public Cleaner(int id, String name, String email, String password, String phone, 
                  String area, String experience, String registrationDate, 
                  String status, int tasksCompleted, float rating, String profilePhoto) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.area = area;
        this.experience = experience;
        this.registrationDate = registrationDate;
        this.status = status;
        this.tasksCompleted = tasksCompleted;
        this.rating = rating;
        this.profilePhoto = profilePhoto;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTasksCompleted() {
        return tasksCompleted;
    }

    public void setTasksCompleted(int tasksCompleted) {
        this.tasksCompleted = tasksCompleted;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
} 