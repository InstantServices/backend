package com.instantservices.backend.dto;

public class RatingRequest {

    private int score;
    private String review;

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getReview() { return review; }
    public void setReview(String review) { this.review = review; }
}
