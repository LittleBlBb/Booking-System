package ru.bookingsystem.DTO.requests;

import lombok.Getter;

@Getter
public class SetCompanySettingsRequest {

    private Integer maxBookingsPerUser;
    private Integer maxBookingDurationMinutes;
}
