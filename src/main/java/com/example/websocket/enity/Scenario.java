package com.example.websocket.enity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}
