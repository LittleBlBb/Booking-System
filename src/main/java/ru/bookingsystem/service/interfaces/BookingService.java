package ru.bookingsystem.service.interfaces;

import ru.bookingsystem.DTO.requests.BookingCreateRequest;
import ru.bookingsystem.DTO.requests.BookingUpdateRequest;
import ru.bookingsystem.entity.Booking;
import ru.bookingsystem.entity.constant.BookingStatus;

import java.util.List;

public interface BookingService {
    Booking editBooking(BookingUpdateRequest request);

    Booking addBooking(BookingCreateRequest request);

    Booking findById(Long id);

    List<Booking> findAll();

    List<Booking> findAllByStatusAndUserId(BookingStatus status, Long userId);

    void deleteById(Long id);

    void cancelAllBookingsByUserId(Long userId);
}
