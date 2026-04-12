package ru.bookingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.bookingsystem.entity.Booking;
import ru.bookingsystem.entity.Resource;

import java.time.LocalDateTime;

public interface BookingRepo extends JpaRepository<Booking, Long> {

    @Query("""
        SELECT COUNT(b)
        FROM Booking b
        WHERE b.resource = :resource
            AND b.status = 'ACTIVE'
            AND b.startTime  < :endTime
            AND b.endTime > :startTime
    """)
    long countOverlappingBookings(
            @Param("resource") Resource resource,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime")LocalDateTime endTime
            );
}
