package ru.bookingsystem.DTO.requests;

import lombok.Data;
import ru.bookingsystem.entity.constant.Role;

@Data
public class RoleUpdateRequest {

    private Long userId;
    private Role role;
}
