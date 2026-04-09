package ru.bookingsystem.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.entity.Company;
import ru.bookingsystem.requests.CompanyCreateRequest;
import ru.bookingsystem.requests.CompanyUpdateRequest;
import ru.bookingsystem.service.interfaces.CompanyService;

import java.util.List;

@RestController()
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/api/companies/all")
    public List<Company> findAll(){

        return companyService.findAll();
    }

    @PostMapping("/api/companies/create")
    public Company addCompany(@RequestBody CompanyCreateRequest request){

        return companyService.addCompany(request);
    }

    @GetMapping("/api/companies/getById")
    public Company findById(@RequestParam Long id){

        return companyService.findById(id);
    }

    @PutMapping("/api/companies/editCompanyById")
    public String editCompany(@RequestBody CompanyUpdateRequest request){

        return companyService.editCompany(request);
    }

    @DeleteMapping("api/companies/deleteById")
    public void deleteById(@RequestParam Long id){

        companyService.deleteById(id);
    }
}
