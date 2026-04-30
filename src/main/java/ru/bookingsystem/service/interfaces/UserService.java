package ru.bookingsystem.service.interfaces;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.bookingsystem.DTO.RegistrationUserDTO;
import ru.bookingsystem.DTO.UserActivationResponse;
import ru.bookingsystem.DTO.requests.RoleUpdateRequest;
import ru.bookingsystem.DTO.requests.UserUpdateRequest;
import ru.bookingsystem.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    User findById(Long id);

    List<User> findAll();

    void deleteById(Long userId);

    User updateUser(UserUpdateRequest request);

    User findByUsername(String username);

    Boolean existsByUsername(String username);

    UserActivationResponse activateUser(String code);

    User updateRole(RoleUpdateRequest request);

    User deleteUserFromCompany(Long id);

    User save(User user);

    User findByEmail(String email);

    Boolean existsByEmail(String email);
}
