package ru.bookingsystem.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.bookingsystem.entity.Company;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private Company company;
    private String username;
    private String email;
}
