package ru.bookingsystem.DTO.requests;

import lombok.Data;

@Data
public class ResourceTypeCreateRequest {

    private String name;
    private Long companyId;
}
