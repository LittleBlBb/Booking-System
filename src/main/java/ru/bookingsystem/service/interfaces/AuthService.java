package ru.bookingsystem.service.interfaces;

import ru.bookingsystem.DTO.AuthResponse;
import ru.bookingsystem.DTO.RegistrationUserDTO;
import ru.bookingsystem.DTO.requests.AuthRequest;
import ru.bookingsystem.entity.User;

public interface AuthService {
    AuthResponse login(AuthRequest request);

    User registration(RegistrationUserDTO request);
}
