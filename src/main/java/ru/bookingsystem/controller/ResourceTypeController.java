package ru.bookingsystem.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.DTO.ResourceTypeDTO;
import ru.bookingsystem.DTO.requests.ResourceTypeCreateRequest;
import ru.bookingsystem.entity.ResourceType;
import ru.bookingsystem.service.interfaces.ResourceTypeService;

import java.util.List;

@RestController
@RequestMapping("/api/resource_types/")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ResourceTypeController {

    private final ResourceTypeService resourceTypeService;

    @PostMapping("/addResourceType")
    public ResourceTypeDTO addResourceType(@RequestBody ResourceTypeCreateRequest request){

        return resourceTypeService.addResourceType(request);
    }

    @GetMapping("/findResourceTypeById")
    public ResourceTypeDTO findById(@RequestParam Long id){

        return resourceTypeService.findById(id);
    }

    @GetMapping("/findAll")
    public List<ResourceTypeDTO> findAll(){

        return resourceTypeService.findAll();
    }
}
