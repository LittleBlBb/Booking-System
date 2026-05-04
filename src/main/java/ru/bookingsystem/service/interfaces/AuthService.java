package ru.bookingsystem.service.interfaces;

import ru.bookingsystem.DTO.AuthResponse;
import ru.bookingsystem.DTO.RegistrationUserDTO;
import ru.bookingsystem.DTO.UserResponseDTO;
import ru.bookingsystem.DTO.requests.AuthRequest;

public interface AuthService {
    AuthResponse login(AuthRequest request);

    UserResponseDTO registration(RegistrationUserDTO request);
}
