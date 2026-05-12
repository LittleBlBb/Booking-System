package ru.bookingsystem.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bookingsystem.entity.ResourceType;

@Data
@NoArgsConstructor
public class ResourceTypeDTO {

    private Long id;
    private Integer iconId;
    private String name;

    public ResourceTypeDTO(ResourceType resourceType){

        this.id = resourceType.getId();
        this.iconId = resourceType.getIconId();
        this.name = resourceType.getName();
    }
}