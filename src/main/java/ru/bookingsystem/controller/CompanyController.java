package ru.bookingsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.entity.Company;
import ru.bookingsystem.DTO.requests.CompanyCreateRequest;
import ru.bookingsystem.DTO.requests.CompanyUpdateRequest;
import ru.bookingsystem.service.interfaces.CompanyService;

import java.util.List;

@Tag(name = "company_methods", description = "operations with company")
@RestController()
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @Operation(
            summary = "find all companies",
            description = "returns all companies DTO"
    )
    @GetMapping("/api/companies/all")
    public List<Company> findAll(){

        return companyService.findAll();
    }

    @Operation(
            summary = "add company to database",
            description = "creating new company by request and save to database in service"

    )
    @PostMapping("/api/companies/create")
    public Company addCompany(@RequestBody CompanyCreateRequest request){

        return companyService.addCompany(request);
    }

    @Operation(
            summary = "find company by id",
            description = "returns company DTO with selected id"
    )
    @GetMapping("/api/companies/getById")
    public Company findById(@RequestParam Long id){

        return companyService.findById(id);
    }

    @Operation(
            summary = "edit company",
            description = "editing company by id, returns new company DTO"
    )
    @PutMapping("/api/companies/editCompanyById")
    public String editCompany(@RequestBody CompanyUpdateRequest request){

        return companyService.editCompany(request);
    }

    @Operation(
            summary = "delete company",
            description = "deleting company by id in service, returns void"
    )
    @DeleteMapping("api/companies/deleteById")
    public void deleteById(@RequestParam Long id){

        companyService.deleteById(id);
    }
}
