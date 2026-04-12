package ru.bookingsystem.service.interfaces;

import ru.bookingsystem.entity.User;
import ru.bookingsystem.DTO.requests.UserCreateRequest;
import ru.bookingsystem.DTO.requests.UserUpdateRequest;

import java.util.List;

public interface UserService {

    User findById(Long id);

    List<User> findAll();

    User addUser(UserCreateRequest request);

    void deleteById(Long userId);

    String updateUser(UserUpdateRequest request);
}
