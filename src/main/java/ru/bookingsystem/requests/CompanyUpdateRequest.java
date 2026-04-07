package ru.bookingsystem.requests;

import lombok.Data;

@Data
public class CompanyUpdateRequest {
    private Long id;
    private String name;
}
