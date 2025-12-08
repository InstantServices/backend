package com.instantservices.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "offers")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double proposedCommission;   // task-doer negotiates price
    private String message;              // message from helper
    private String expectedTime;         // like "30 min", "1 hour"

    private String status = "PENDING";   // PENDING, ACCEPTED, REJECTED

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user; // The task-doer who sent the proposal
}
