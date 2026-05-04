package ru.bookingsystem.service.interfaces;

import org.springframework.security.core.Authentication;
import ru.bookingsystem.DTO.BookingDTO;
import ru.bookingsystem.DTO.requests.BookingCreateRequest;
import ru.bookingsystem.DTO.requests.BookingUpdateRequest;
import ru.bookingsystem.entity.Booking;
import ru.bookingsystem.entity.constant.BookingStatus;

import java.util.List;

public interface BookingService {
    BookingDTO editBooking(Authentication authentication, BookingUpdateRequest request);

    BookingDTO addBooking(Authentication authentication, BookingCreateRequest request);

    BookingDTO findById(Long id);

    List<BookingDTO> findAll();

    List<Booking> findAllByStatusAndUserId(BookingStatus status, Long userId);

    void deleteById(Authentication authentication, Long id);

    void cancelAllBookingsByUserId(Long userId);

    List<BookingDTO> findAllByResourceId(Long resourceId);

    List<BookingDTO> findAllByResourceIdAndStatus(Long resourceId, BookingStatus status);

    List<BookingDTO> findAllByCompanyId(Long companyId);

    List<BookingDTO> findAllByCompanyIdAndStatus(Long companyId, BookingStatus status);
}
