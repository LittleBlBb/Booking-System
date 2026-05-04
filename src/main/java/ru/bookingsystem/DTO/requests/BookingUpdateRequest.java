package ru.bookingsystem.DTO.requests;

import lombok.Data;
import ru.bookingsystem.entity.constant.BookingStatus;

import java.time.LocalDateTime;

@Data
public class BookingUpdateRequest {

    private Long bookingId;
    private Long resourceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BookingStatus status;
}
