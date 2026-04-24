package ru.bookingsystem.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.DTO.AuthResponse;
import ru.bookingsystem.DTO.UserActivationResponse;
import ru.bookingsystem.DTO.requests.AuthRequest;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.service.interfaces.AuthService;
import ru.bookingsystem.service.interfaces.UserService;

@Tag(name = "auth_methods", description = "auth operations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "bearerAuth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request){

        return authService.login(request);
    }

    @GetMapping("/me")
    public User userData(Authentication authentication){

        return userService.findByUsername(authentication.getName());
    }

    @GetMapping("/activate/{code}")
    public UserActivationResponse activate(@PathVariable String code){

        return userService.activateUser(code);
    }
}

