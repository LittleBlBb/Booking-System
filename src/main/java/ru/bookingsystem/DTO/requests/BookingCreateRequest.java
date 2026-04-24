package ru.bookingsystem.DTO.requests;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingCreateRequest {

    private Long userId;
    private Long resourceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
