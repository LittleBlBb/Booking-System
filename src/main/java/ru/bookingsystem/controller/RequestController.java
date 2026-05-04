package ru.bookingsystem.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.DTO.CompanyJoinRequestDTO;
import ru.bookingsystem.service.interfaces.CompanyJoinRequestService;

import java.util.List;

@Tag(name = "requests_methods", description = "operations with requests")
@RestController
@RequestMapping("/api/")
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class RequestController {

    private final CompanyJoinRequestService companyJoinRequestService;

    @PostMapping("/requests/approve")
    public void approve(Authentication authentication, @RequestParam Long id){

        companyJoinRequestService.approve(authentication, id);
    }

    @PostMapping("/requests/reject")
    public void reject(Authentication authentication, @RequestParam Long id){

        companyJoinRequestService.reject(authentication, id);
    }


    @GetMapping("/requests")
    public List<CompanyJoinRequestDTO> getAllRequestsById(Authentication authentication){

        return companyJoinRequestService.getAllById(authentication.getName());
    }

    @PostMapping("/company/join-request")
    public CompanyJoinRequestDTO joinRequest(Authentication authentication, @RequestParam Long id){

        return companyJoinRequestService.joinRequest(authentication, id);
    }

}
