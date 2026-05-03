package ru.bookingsystem.DTO;

import lombok.Data;
import ru.bookingsystem.entity.Company;

@Data
public class CompanyDTO {

    private Long id;
    private String name;

    public CompanyDTO(Company company){

        this.id = company.getId();
        this.name = company.getName();
    }
}
