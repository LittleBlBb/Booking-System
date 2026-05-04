package ru.bookingsystem.service.implementation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.bookingsystem.repository.BookingRepo;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingSchedulerService {

    private final BookingRepo bookingRepo;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expireBookings() {
        LocalDateTime now = LocalDateTime.now();
        bookingRepo.expiredOldBookings(now);
    }

}
