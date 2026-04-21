package ru.bookingsystem.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.DTO.AuthResponse;
import ru.bookingsystem.DTO.UserActivationResponse;
import ru.bookingsystem.DTO.requests.AuthRequest;
import ru.bookingsystem.service.interfaces.AuthService;
import ru.bookingsystem.service.interfaces.UserService;

import java.security.Principal;

@Tag(name = "auth_methods", description = "auth operations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

//    @PostMapping("/registration")
//    public

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request){

        return authService.login(request);
    }

    @GetMapping("/me")
    public String userData(Principal principal){

        return principal.getName();
    }

    @GetMapping("/activate/{code}")
    public UserActivationResponse activate(@PathVariable String code){

        return userService.activateUser(code);
    }
}

