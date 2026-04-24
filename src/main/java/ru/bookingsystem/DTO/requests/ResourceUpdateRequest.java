package ru.bookingsystem.DTO.requests;

import lombok.Data;

@Data
public class ResourceUpdateRequest {

    private Long id;
    private String name;
    private String description;
    private Long resourceTypeId;
    private Long companyId;
    private Integer quantity;
}
