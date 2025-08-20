package com.s23010169.ecowastereporter.models;

// This class represents a citizen user account in the app.
public class Citizen {
    private int id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String registrationDate;
    private String profilePhoto;

    // Default constructor
    public Citizen() {
    }

    // Constructor without id (for new records)
    public Citizen(String name, String email, String password, String phone, String address) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
    }

    // Constructor with all fields
    public Citizen(int id, String name, String email, String password, String phone, String address, String registrationDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.registrationDate = registrationDate;
    }

    // Constructor with all fields including profile photo
    public Citizen(int id, String name, String email, String password, String phone, String address, String registrationDate, String profilePhoto) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.registrationDate = registrationDate;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
} 