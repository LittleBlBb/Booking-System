package ru.bookingsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Clock;
import java.time.LocalTime;

@Entity
@Table(name = "company_settings")
@Setter
@Getter
@NoArgsConstructor
public class CompanySettings {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "company_id")
    private Company company;

    private Integer maxBookingsPerUser;
    private Integer maxBookingDurationMinutes;
    private LocalTime workStart;
    private LocalTime workEnd;
}
