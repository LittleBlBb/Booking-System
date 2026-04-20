package ru.bookingsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.DTO.RegistrationUserDTO;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.DTO.requests.UserUpdateRequest;
import ru.bookingsystem.service.interfaces.UserService;

import java.util.List;

@Tag(name = "user_methods", description = "operations with user")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "find user by id",
            description = "returns user DTO with selected id"
    )
    @GetMapping("/getById")
    public User getUserById(@RequestParam Long userId){

        return userService.findById(userId);
    }

    @Operation(
            summary = "find all users",
            description = "returns all users DTO"
    )
    @GetMapping("/all")
    public List<User> findAll(){

        return userService.findAll();
    }

    @Operation(
            summary = "add user to database",
            description = "creating new user by request and save to database in service"
    )
    @PostMapping("/addUser")
    public User addUser(@RequestBody RegistrationUserDTO request){

        return userService.addUser(request);
    }

    @Operation(
            summary = "delete user",
            description = "deleting user by id in service, returns void"
    )
    @DeleteMapping("/deleteById")
    public void deleteUser(@RequestParam Long userId){

        userService.deleteById(userId);
    }

    @Operation(
            summary = "edit user",
            description = "editing user by id in service, returns new company DTO"
    )
    @PutMapping("/updateUser")
    public String editUser(@RequestBody UserUpdateRequest request){

        return userService.updateUser(request);
    }
}
