package ru.bookingsystem.service.interfaces;

import org.springframework.security.core.Authentication;
import ru.bookingsystem.DTO.CompanySettingsDTO;
import ru.bookingsystem.DTO.requests.SetCompanySettingsRequest;
import ru.bookingsystem.entity.CompanySettings;

public interface CompanySettingsService {
    CompanySettingsDTO getSettings(Authentication authentication);

    CompanySettingsDTO addSettings(Authentication authentication, SetCompanySettingsRequest request);

    CompanySettingsDTO updateSettings(Authentication authentication, SetCompanySettingsRequest request);

    CompanySettings findSettingsByCompanyId(Long id);
}
