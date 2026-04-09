package ru.bookingsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.requests.UserCreateRequest;
import ru.bookingsystem.requests.UserUpdateRequest;
import ru.bookingsystem.service.interfaces.UserService;

import java.util.List;

@RestController()
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/users/getById")
    public User getUserById(@RequestParam Long userId){

        return userService.findById(userId);
    }

    @GetMapping("/api/users/all")
    public List<User> findAll(){

        return userService.findAll();
    }

    @PostMapping("/api/users/addUser")
    public User addUser(@RequestBody UserCreateRequest request){

        return userService.addUser(request);
    }

    @DeleteMapping("/api/users/deleteById")
    public void deleteUser(@RequestParam Long userId){

        userService.deleteById(userId);
    }

    @PutMapping("/api/users/updateUser")
    public String editUser(@RequestBody UserUpdateRequest request){

        return userService.updateUser(request);
    }
}
