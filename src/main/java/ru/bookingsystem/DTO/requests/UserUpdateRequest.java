package ru.bookingsystem.DTO.requests;

import lombok.Data;
import ru.bookingsystem.entity.constant.Role;

@Data
public class UserUpdateRequest {
    private Long id;
    private Long companyId;
    private String username;
    private Role role;
}
