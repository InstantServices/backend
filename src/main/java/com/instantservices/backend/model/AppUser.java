package com.instantservices.backend.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users")
public class AppUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;

    @Column(nullable = false)
    private String password;

    // Profile fields
    private String phone;               // phone number (optional)
    private String city;                // city/locality
    private String country;             // country
    private Double latitude;            // optional GPS
    private Double longitude;           // optional GPS
    private String bio;
    // short bio
    @Column(name = "wallet_balance")
    private Double walletBalance = 0.0;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }




    @Column(name = "total_earnings")
    private Double totalEarnings = 0.0;

    private String role;

    private Double averageRating = 0.0;
    private Integer totalRatings = 0;

    private Integer tasksAccepted = 0;
    private Integer tasksCompleted = 0;
    private Integer tasksPosted =0;

    private Integer disputes = 0;
    private Integer cancellations = 0;
    private Integer noResponseCount = 0;

    @Column(name="trust_score")
    private Double trustScore = 50.0;

    private Boolean banned = false;


    // Constructors
    public AppUser() {}

    // getters & setters (generated)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

   // public Integer getReliabilityScore() { return reliabilityScore; }
    //public void setReliabilityScore(Integer reliabilityScore) { this.reliabilityScore = reliabilityScore; }

    public Integer getTasksCompleted() { return tasksCompleted; }
    public void setTasksCompleted(Integer tasksCompleted) { this.tasksCompleted = tasksCompleted; }

    public Integer getTasksPosted() { return tasksPosted; }
    public void setTasksPosted(Integer tasksPosted) { this.tasksPosted = tasksPosted; }

    public Double getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(Double totalEarnings) { this.totalEarnings = totalEarnings; }

    public Double getTrustScore() {
        return trustScore;
    }

    public void setTrustScore(Double trustScore) {
        this.trustScore = trustScore;
    }

    public Integer getDisputes() {
        return disputes;
    }

    public void setDisputes(Integer disputes) {
        this.disputes = disputes;
    }

    public Integer getTasksAccepted() {
        return tasksAccepted;
    }

    public void setTasksAccepted(Integer tasksAccepted) {
        this.tasksAccepted = tasksAccepted;
    }

    public Integer getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(Integer totalRatings) {
        this.totalRatings = totalRatings;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getNoResponseCount() {
        return noResponseCount;
    }

    public void setNoResponseCount(Integer noResponseCount) {
        this.noResponseCount = noResponseCount;
    }

    public Integer getCancellations() {
        return cancellations;
    }

    public void setCancellations(Integer cancellations) {
        this.cancellations = cancellations;
    }

    public Double getWalletBalance() {
        return walletBalance==null?0.0:walletBalance;
    }

    public void setWalletBalance(Double walletBalance) {
        this.walletBalance = walletBalance;
    }

    public Boolean getBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }
}
