package ru.bookingsystem.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.bookingsystem.DTO.AuthResponse;
import ru.bookingsystem.DTO.requests.AuthRequest;
import ru.bookingsystem.service.interfaces.AuthService;
import ru.bookingsystem.service.interfaces.UserService;
import ru.bookingsystem.util.CustomUserDetails;
import ru.bookingsystem.util.JwtUtils;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @Override
    public AuthResponse login(AuthRequest request){

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));

        CustomUserDetails userDetails = (CustomUserDetails) userService.loadUserByUsername(request.getLogin());
        String token = jwtUtils.generateToken(userDetails);

        return new AuthResponse(userDetails.getUsername(), userDetails.getEmail(), token);
    }
}
