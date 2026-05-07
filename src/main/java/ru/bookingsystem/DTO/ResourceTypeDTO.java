package ru.bookingsystem.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bookingsystem.entity.ResourceType;

@Data
@NoArgsConstructor
public class ResourceTypeDTO {

    private Long id;
    private String name;

    public ResourceTypeDTO(ResourceType resourceType){

        this.id = resourceType.getId();
        this.name = resourceType.getName();
    }
}