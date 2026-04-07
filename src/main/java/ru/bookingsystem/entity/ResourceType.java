package ru.bookingsystem.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "resource_types")
public class ResourceType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE) // Для Postgres рекомендуется использовать SEQUENCE стратегию
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
}
