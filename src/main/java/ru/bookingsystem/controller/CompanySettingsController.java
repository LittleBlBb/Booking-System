package ru.bookingsystem.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.DTO.CompanySettingsDTO;
import ru.bookingsystem.DTO.requests.SetCompanySettingsRequest;
import ru.bookingsystem.service.interfaces.CompanySettingsService;

@RestController
@RequestMapping("/api/company_settings")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CompanySettingsController {

    private final CompanySettingsService companySettingsService;

    @GetMapping("/getCompanySettings")
    public CompanySettingsDTO getSettings(Authentication authentication){

        return companySettingsService.getSettings(authentication);
    }

    @PostMapping("/addSettings")
    public CompanySettingsDTO createSettings(Authentication authentication, @RequestBody SetCompanySettingsRequest request){
        return companySettingsService.addSettings(authentication, request);
    }

    @PatchMapping("/updateSettings")
    public CompanySettingsDTO editSettings(Authentication authentication, @RequestBody SetCompanySettingsRequest request){
        return companySettingsService.updateSettings(authentication, request);
    }
}
