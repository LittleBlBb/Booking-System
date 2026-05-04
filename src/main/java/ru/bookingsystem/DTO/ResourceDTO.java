package ru.bookingsystem.DTO;

import lombok.Data;
import ru.bookingsystem.entity.Booking;
import ru.bookingsystem.entity.Resource;

@Data
public class ResourceDTO {

    private Long id;
    private String name;
    private String description;
    private Long company_id;
    private Long type_id;
    private Integer quantity;


    public ResourceDTO(Resource resource){
        this.id = resource.getId();
        this.name = resource.getName();
        this.description = resource.getDescription();
        this.company_id = resource.getCompany().getId();
        this.type_id = resource.getType().getId();
        this.quantity = resource.getQuantity();
    }
}
