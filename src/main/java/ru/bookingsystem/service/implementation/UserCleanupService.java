package ru.bookingsystem.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bookingsystem.service.interfaces.BookingService;

@Service
@RequiredArgsConstructor
public class UserCleanupService {

    private final BookingService bookingService;

    public void handleUserLeaving(Long userId){
        bookingService.cancelAllBookingsByUserId(userId);
    }
}
