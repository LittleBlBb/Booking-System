package ru.bookingsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bookingsystem.DTO.AuthResponse;
import ru.bookingsystem.DTO.RegistrationUserDTO;
import ru.bookingsystem.DTO.UserResponseDTO;
import ru.bookingsystem.DTO.requests.AuthRequest;
import ru.bookingsystem.service.interfaces.AuthService;

@Tag(name = "auth_methods", description = "auth operations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "bearerAuth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request){

        return authService.login(request);
    }

    @Operation(
            summary = "add user to database",
            description = "creating new user by request and save to database in service"
    )
    @PostMapping("/register")
    public UserResponseDTO addUser(@RequestBody RegistrationUserDTO request){

        return authService.registration(request);
    }
}

