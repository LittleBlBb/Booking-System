package ru.bookingsystem.requests;

import lombok.Data;
import ru.bookingsystem.entity.Role;

@Data
public class UserCreateRequest {
    private Long companyId;
    private String username;
    private Role role;
}