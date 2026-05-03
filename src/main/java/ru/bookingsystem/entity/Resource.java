package ru.bookingsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
    @JoinColumn(name = "type_id", nullable = false)
    private ResourceType type;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    private Integer quantity;

    @OneToMany(
            mappedBy = "resource",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Booking> bookings;
}
