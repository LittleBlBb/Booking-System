package ru.bookingsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "resources")
@Getter
@Setter
@NoArgsConstructor
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

    private Integer quantity;
}
