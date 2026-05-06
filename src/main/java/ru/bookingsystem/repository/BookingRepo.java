package ru.bookingsystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.bookingsystem.DTO.BookingDTO;
import ru.bookingsystem.entity.Booking;
import ru.bookingsystem.entity.Resource;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.entity.constant.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

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

    List<Booking> findAllByStatusAndUserId(BookingStatus bookingStatus, Long userId);

    @Query("""
        SELECT COUNT(b)
        FROM Booking b
        WHERE b.user = :user
            AND b.status = 'ACTIVE'
            AND b.startTime  < :endTime
            AND b.endTime > :startTime

""")
    long countUserBookingsInInterval(
            @Param("user") User user,
            @Param("startTime") LocalDateTime start,
            @Param("endTime") LocalDateTime end);

    List<Booking> findAllByResourceId(Long resourceId);

    List<Booking> findAllByResourceIdAndStatus(Long resourceId, BookingStatus status);

    @Modifying
    @Query("""
        UPDATE Booking b
        SET b.status = 'EXPIRED'
        WHERE b.status = 'ACTIVE'
            AND b.endTime < :now
""")
    void expiredOldBookings(@Param("now") LocalDateTime now);

    @Query("""
        SELECT b
        FROM Booking b
        JOIN b.resource r
        WHERE r.company.id = :companyId
            AND b.status = :status
""")
    List<Booking> findAllByCompanyIdAndStatus(@Param("companyId") Long companyId,
                               @Param("status") BookingStatus status);

    @Query("""
        SELECT b
        FROM Booking b
        JOIN b.resource r
        WHERE r.company.id = :companyId
""")
    List<Booking> findAllByCompanyId(Long companyId);

    Page<Booking> findAllByUserId(Long userId, Pageable pageable);

    Page<Booking> findAllByUserIdAndStatus(Long userId, Pageable pageable, BookingStatus status);
}
