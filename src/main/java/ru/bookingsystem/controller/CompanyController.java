package ru.bookingsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.DTO.CompanyDTO;
import ru.bookingsystem.DTO.requests.CompanyCreateRequest;
import ru.bookingsystem.DTO.requests.CompanyUpdateRequest;
import ru.bookingsystem.service.interfaces.CompanyService;

import java.util.List;

@Tag(name = "company_methods", description = "operations with company")
@RestController
@RequestMapping("/api/companies")
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CompanyController {

    private final CompanyService companyService;

    @Operation(
            summary = "find all companies",
            description = "returns all companies DTO"
    )
    @GetMapping("/all")
    public List<CompanyDTO> findAll(){

        return companyService.findAll();
    }

    @Operation(
            summary = "add company to database",
            description = "creating new company by request and save to database in service"

    )
    @PostMapping("/create")
    public CompanyDTO addCompany(@RequestBody CompanyCreateRequest request, Authentication authentication){

        return companyService.addCompany(authentication, request);
    }

    @Operation(
            summary = "find company by id",
            description = "returns company DTO with selected id"
    )
    @GetMapping("/getById")
    public CompanyDTO findById(@RequestParam Long id){

        return new CompanyDTO(companyService.findById(id));
    }

    @Operation(
            summary = "edit company",
            description = "editing company by id, returns new company DTO"
    )
    @PutMapping("/editCompanyById")
    public CompanyDTO editCompany(Authentication authentication, @RequestBody CompanyUpdateRequest request){

        return companyService.editCompany(authentication, request);
    }

    @Operation(
            summary = "delete company",
            description = "deleting company by id in service, returns void"
    )
    @DeleteMapping("/deleteById")
    public void deleteById(Authentication authentication, @RequestParam Long id){

        companyService.deleteById(authentication, id);
    }
}
