package ru.bookingsystem.DTO.requests;

import lombok.Data;
import ru.bookingsystem.entity.constant.Role;

@Data
public class UserCreateRequest {
    private Long companyId;
    private String username;
    private String password;
    private String passwordConfirm;
    private String email;
    private Role role;
}