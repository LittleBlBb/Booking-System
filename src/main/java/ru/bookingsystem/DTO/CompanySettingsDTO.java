package ru.bookingsystem.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bookingsystem.entity.CompanySettings;

import java.time.LocalTime;

@Data
@NoArgsConstructor
public class CompanySettingsDTO {

    private Long companyId;
    private Integer maxBookingsPerUser;
    private Integer maxBookingDurationMinutes;
    private LocalTime workStart;
    private LocalTime workEnd;

    public CompanySettingsDTO(CompanySettings settings){

        this.companyId = settings.getCompany().getId();
        this.maxBookingsPerUser = settings.getMaxBookingsPerUser();
        this.maxBookingDurationMinutes = settings.getMaxBookingDurationMinutes();
        this.workStart = settings.getWorkStart();
        this.workEnd = settings.getWorkEnd();
    }
}
