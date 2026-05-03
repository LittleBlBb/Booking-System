package ru.bookingsystem.DTO.requests;

import lombok.Data;

@Data
public class UserUpdateRequest {

    private String username;
    private String currentPassword;
    private String password;
    private String confirmPassword;
    private String email;
}
