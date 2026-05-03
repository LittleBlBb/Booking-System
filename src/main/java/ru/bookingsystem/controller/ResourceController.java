package ru.bookingsystem.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.DTO.ResourceDTO;
import ru.bookingsystem.DTO.requests.ResourceCreateRequest;
import ru.bookingsystem.DTO.requests.ResourceUpdateRequest;
import ru.bookingsystem.entity.Resource;
import ru.bookingsystem.service.interfaces.ResourceService;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping("/addResource")
    public ResourceDTO addResource(@RequestBody ResourceCreateRequest request){

        return resourceService.addResource(request);
    }

    @GetMapping("/findResourceById")
    public ResourceDTO findById(@RequestParam Long id){

        return resourceService.findById(id);
    }

    @GetMapping("/findAll")
    public List<ResourceDTO> findAll(){

        return resourceService.findAll();
    }

    @GetMapping("/findAll/{companyId}")
    public List<ResourceDTO> findAllByCompanyId(@RequestParam Long companyId){

        return resourceService.findAllByCompanyId(companyId);
    }

    @DeleteMapping("/deleteById")
    public void deleteById(@RequestParam Long id){

        resourceService.deleteById(id);
    }

    @PutMapping("/editResource")
    public ResourceDTO editResource(@RequestBody ResourceUpdateRequest request){

        return resourceService.editResource(request);
    }
}
