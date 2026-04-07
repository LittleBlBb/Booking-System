package ru.bookingsystem.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "resources")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE) // Для Postgres рекомендуется использовать SEQUENCE стратегию
    private Long id;

    private String name;

    private String description;

    @ManyToOne
    private ResourceType type;

    @ManyToOne
    private Company company;
}
