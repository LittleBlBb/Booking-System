package ru.bookingsystem.service.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.bookingsystem.DTO.RegistrationUserDTO;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.DTO.requests.UserUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    User findById(Long id);

    List<User> findAll();

    User addUser(RegistrationUserDTO request);

    void deleteById(Long userId);

    String updateUser(UserUpdateRequest request);

    Optional<User> findByUsername(String username);

}
