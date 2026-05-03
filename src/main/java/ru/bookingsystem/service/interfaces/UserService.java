package ru.bookingsystem.service.interfaces;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.bookingsystem.DTO.UserActivationResponse;
import ru.bookingsystem.DTO.UserResponseDTO;
import ru.bookingsystem.DTO.requests.RoleUpdateRequest;
import ru.bookingsystem.DTO.requests.UserUpdateRequest;
import ru.bookingsystem.entity.User;

import java.util.List;

public interface UserService extends UserDetailsService {

    UserResponseDTO findById(Long id);

    List<UserResponseDTO> findAll();

    void delete(Authentication authentication);

    UserResponseDTO updateUser(Authentication authentication, UserUpdateRequest request);

    User findByUsername(String username);

    Boolean existsByUsername(String username);

    UserActivationResponse activateUser(String code);

    UserResponseDTO updateRole(RoleUpdateRequest request);

    UserResponseDTO deleteUserFromCompany(Long id);

    UserResponseDTO save(User user);

    User findByEmail(String email);

    Boolean existsByEmail(String email);

    UserResponseDTO leaveCompany(Authentication authentication);
}
