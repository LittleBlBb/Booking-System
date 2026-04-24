package ru.bookingsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "resource_types")
@Getter
@Setter
@NoArgsConstructor
public class ResourceType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE) // Для Postgres рекомендуется использовать SEQUENCE стратегию
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
}
