package ru.bookingsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "resource_types",
        uniqueConstraints = @UniqueConstraint(columnNames = {"company_id", "name"})
)
@Getter
@Setter
@NoArgsConstructor
public class ResourceType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE) // Для Postgres рекомендуется использовать SEQUENCE стратегию
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}
