package ru.bookingsystem.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.bookingsystem.DTO.CompanySettingsDTO;
import ru.bookingsystem.DTO.requests.SetCompanySettingsRequest;
import ru.bookingsystem.entity.CompanySettings;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.repository.CompanySettingsRepo;
import ru.bookingsystem.service.interfaces.CompanySettingsService;
import ru.bookingsystem.service.interfaces.UserService;

@Service
@RequiredArgsConstructor
public class CompanySettingsServiceImpl implements CompanySettingsService {

    private final CompanySettingsRepo companySettingsRepo;
    private final UserService userService;

    @Override
    public CompanySettingsDTO getSettings(Authentication authentication) {

        User user = userService.findByUsername(authentication.getName());
        CompanySettings settings = findSettingsByCompanyId(user.getCompany().getId());

        if (settings == null){
            return new CompanySettingsDTO();
        }

        return new CompanySettingsDTO(settings);
    }

    @Override
    public CompanySettingsDTO addSettings(Authentication authentication, SetCompanySettingsRequest request) {

        User user = userService.findByUsername(authentication.getName());

        CompanySettings settings = new CompanySettings();
        settings.setCompany(user.getCompany());
        settings.setMaxBookingsPerUser(request.getMaxBookingsPerUser());
        settings.setMaxBookingDurationMinutes(request.getMaxBookingDurationMinutes());
        settings.setWorkStart(request.getWorkStart());
        settings.setWorkEnd(request.getWorkEnd());

        return new CompanySettingsDTO(companySettingsRepo.save(settings));
    }

    @Override
    public CompanySettingsDTO updateSettings(Authentication authentication, SetCompanySettingsRequest request){

        User user = userService.findByUsername(authentication.getName());

        CompanySettings settings = companySettingsRepo.findCompanySettingsByCompanyId(user.getCompany().getId());

        settings.setCompany(user.getCompany());
        settings.setMaxBookingsPerUser(request.getMaxBookingsPerUser());
        settings.setMaxBookingDurationMinutes(request.getMaxBookingDurationMinutes());
        settings.setWorkStart(request.getWorkStart());
        settings.setWorkEnd(request.getWorkEnd());

        return new CompanySettingsDTO(companySettingsRepo.save(settings));
    }

    @Override
    public CompanySettings findSettingsByCompanyId(Long id){
        return companySettingsRepo.findCompanySettingsByCompanyId(id);
    }
}
