package com.instantservices.backend.dto;


public class CreateTaskRequest {
    private String title;
    private String description;
    private String category;
    private Double offeredPrice;
    private Double commission;
    private String city;
    private Double latitude;
    private Double longitude;

    public CreateTaskRequest() {}

    // getters & setters...
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Double getOfferedPrice() { return offeredPrice; }
    public void setOfferedPrice(Double offeredPrice) { this.offeredPrice = offeredPrice; }
    public Double getCommission() { return commission; }
    public void setCommission(Double commission) { this.commission = commission; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}

