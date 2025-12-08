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
    private String bio;                 // short bio

    // Metrics & trust
    @Column(name = "reliability_score")
    private Integer reliabilityScore = 100; // default 100

    @Column(name = "tasks_completed")
    private Integer tasksCompleted = 0;

    @Column(name = "tasks_posted")
    private Integer tasksPosted = 0;

    @Column(name = "total_earnings")
    private Double totalEarnings = 0.0;

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

    public Integer getReliabilityScore() { return reliabilityScore; }
    public void setReliabilityScore(Integer reliabilityScore) { this.reliabilityScore = reliabilityScore; }

    public Integer getTasksCompleted() { return tasksCompleted; }
    public void setTasksCompleted(Integer tasksCompleted) { this.tasksCompleted = tasksCompleted; }

    public Integer getTasksPosted() { return tasksPosted; }
    public void setTasksPosted(Integer tasksPosted) { this.tasksPosted = tasksPosted; }

    public Double getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(Double totalEarnings) { this.totalEarnings = totalEarnings; }
}
