package ru.bookingsystem.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
    public Resource addResource(@RequestParam ResourceCreateRequest request){

        return resourceService.addResource(request);
    }

    @GetMapping("/findResourceById")
    public Resource findById(@RequestParam Long id){

        return resourceService.findById(id);
    }

    @GetMapping("/findAll")
    public List<Resource> findAll(){

        return resourceService.findAll();
    }

    @GetMapping("/findAll/{companyId}")
    public List<Resource> findAllByCompanyId(@RequestParam Long companyId){

        return resourceService.findAllByCompanyId(companyId);
    }

    @DeleteMapping("/deleteById")
    public void deleteById(@RequestParam Long id){

        resourceService.deleteById(id);
    }

    @PutMapping("/editResource")
    public Resource editResource(@RequestBody ResourceUpdateRequest request){

        return resourceService.editResource(request);
    }
}
