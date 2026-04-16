package ru.bookingsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.bookingsystem.DTO.AuthDTO;
import ru.bookingsystem.DTO.JwtResponse;
import ru.bookingsystem.exception.AppError;
import ru.bookingsystem.service.interfaces.UserService;
import ru.bookingsystem.util.JwtUtils;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/auth")
    public ResponseEntity<?> auth(@RequestBody AuthDTO authDTO){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getLogin(), authDTO.getPassword()));
        } catch (BadCredentialsException e) {

            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Bad login or password"), HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = userService.loadUserByUsername(authDTO.getLogin());
        String token = jwtUtils.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    public String userData(Principal principal){

        return principal.getName();
    }
}
