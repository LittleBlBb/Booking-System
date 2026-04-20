package ru.bookingsystem.service.interfaces;

import ru.bookingsystem.DTO.AuthResponse;
import ru.bookingsystem.DTO.requests.AuthRequest;

public interface AuthService {
    AuthResponse login(AuthRequest request);
}
