package ru.bookingsystem.DTO;

import lombok.Data;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.entity.constant.Role;

@Data
public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;
    private Long companyId;
    private String companyName;
    private Role role;

    public UserResponseDTO(User user){

        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.companyId = user.getCompany() == null ? null : user.getCompany().getId();
        this.companyName = user.getCompany() == null ? null : user.getCompany().getName();
        this.role = user.getRole();
    }
}