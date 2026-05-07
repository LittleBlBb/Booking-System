package ru.bookingsystem.DTO.requests;

import lombok.Data;

import java.time.LocalTime;

@Data
public class SetCompanySettingsRequest {

    private Integer maxBookingsPerUser;
    private Integer maxBookingDurationMinutes;
    private LocalTime workStart;
    private LocalTime workEnd;
}
