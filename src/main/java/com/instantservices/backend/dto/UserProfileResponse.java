package com.instantservices.backend.dto;

public class UserProfileResponse {
    private Long id;
    private String email;
    private String name;
    private String phone;
    private String city;
    private String country;
    private Double latitude;
    private Double longitude;
    private String bio;
    private Integer reliabilityScore;
    private Integer tasksCompleted;
    private Integer tasksPosted;
    private Double totalEarnings;

    public UserProfileResponse() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Integer getReliabilityScore() {
        return reliabilityScore;
    }

    public void setReliabilityScore(Integer reliabilityScore) {
        this.reliabilityScore = reliabilityScore;
    }

    public Integer getTasksCompleted() {
        return tasksCompleted;
    }

    public void setTasksCompleted(Integer tasksCompleted) {
        this.tasksCompleted = tasksCompleted;
    }

    public Integer getTasksPosted() {
        return tasksPosted;
    }

    public void setTasksPosted(Integer tasksPosted) {
        this.tasksPosted = tasksPosted;
    }

    public Double getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(Double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    // constructor
    public UserProfileResponse(Long id, String email, String name, String phone, String city, String country,
                               Double latitude, Double longitude, String bio, Integer reliabilityScore,
                               Integer tasksCompleted, Integer tasksPosted, Double totalEarnings) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bio = bio;
        this.reliabilityScore = reliabilityScore;
        this.tasksCompleted = tasksCompleted;
        this.tasksPosted = tasksPosted;
        this.totalEarnings = totalEarnings;
    }

    // getters & setters (omitted for brevity; generate in your IDE)
    // ... please generate them to compile
    // (or I can paste them if you'd like)
}
