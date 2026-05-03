package ru.bookingsystem.DTO;

import lombok.Data;
import ru.bookingsystem.entity.ResourceType;

@Data
public class ResourceTypeDTO {

    private Long id;
    private String name;

    public ResourceTypeDTO(ResourceType resourceType){

        this.id = resourceType.getId();
        this.name = resourceType.getName();
    }
}