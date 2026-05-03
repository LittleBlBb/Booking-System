package ru.bookingsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.DTO.UserActivationResponse;
import ru.bookingsystem.DTO.UserResponseDTO;
import ru.bookingsystem.DTO.requests.RoleUpdateRequest;
import ru.bookingsystem.DTO.requests.UserUpdateRequest;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.service.interfaces.UserService;

import java.util.List;

@Tag(name = "user_methods", description = "operations with user")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "find user by id",
            description = "returns user DTO with selected id"
    )
    @GetMapping("/getById")
    public UserResponseDTO getUserById(@RequestParam Long userId){

        return userService.findById(userId);
    }

    @Operation(
            summary = "find all users",
            description = "returns all users DTO"
    )
    @GetMapping("/all")
    public List<UserResponseDTO> findAll(){

        return userService.findAll();
    }

    @Operation(
            summary = "delete user",
            description = "deleting user by id in service, returns void"
    )
    @DeleteMapping("/delete")
    public void deleteUser(Authentication authentication){

        userService.delete(authentication);
    }

    @Operation(
            summary = "edit user",
            description = "editing user by id in service, returns new company DTO"
    )
    @PutMapping("/updateUser")
    public UserResponseDTO editUser(Authentication authentication, @RequestBody UserUpdateRequest request){

        return userService.updateUser(authentication, request);
    }

    @PutMapping("/updateUserRole")
    public UserResponseDTO updateRole(@RequestBody RoleUpdateRequest request){

        return userService.updateRole(request);
    }

    @DeleteMapping("/deleteUserFromCompany")
    public UserResponseDTO deleteUserFromCompany(@RequestParam Long id){

        return userService.deleteUserFromCompany(id);
    }

    @DeleteMapping("/leave")
    public UserResponseDTO leaveCompany(Authentication authentication){

        return userService.leaveCompany(authentication);
    }

    @GetMapping("/activate/{code}")
    public UserActivationResponse activate(@PathVariable String code){

        return userService.activateUser(code);
    }

    @GetMapping("/me")
    public UserResponseDTO userData(Authentication authentication){

        return new UserResponseDTO(userService.findByUsername(authentication.getName()));
    }
}
