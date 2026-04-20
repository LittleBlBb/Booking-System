package ru.bookingsystem.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.DTO.requests.AuthRequest;
import ru.bookingsystem.DTO.AuthResponse;
import ru.bookingsystem.service.interfaces.AuthService;

import java.security.Principal;

@Tag(name = "auth_methods", description = "auth operations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

//    @PostMapping("/registration")
//    public

    @PostMapping("/auth")
    public AuthResponse auth(@RequestBody AuthRequest request){

        return authService.login(request);
    }

    @GetMapping("/me")
    public String userData(Principal principal){

        return principal.getName();
    }
}

