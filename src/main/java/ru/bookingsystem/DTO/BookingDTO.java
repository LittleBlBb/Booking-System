package ru.bookingsystem.DTO;

import lombok.Data;
import ru.bookingsystem.entity.Booking;
import ru.bookingsystem.entity.constant.BookingStatus;

import java.time.LocalDateTime;

@Data
public class BookingDTO {

    private Long id;
    private Long userId;
    private Long resourceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BookingStatus status;

    public BookingDTO(Booking booking){

        this.id = booking.getId();
        this.userId = booking.getUser().getId();
        this.resourceId = booking.getResource().getId();
        this.startTime = booking.getStartTime();
        this.endTime = booking.getEndTime();
        this.status = booking.getStatus();
    }
}
