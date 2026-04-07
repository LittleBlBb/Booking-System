package ru.bookingsystem.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.entity.Company;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.repository.CompanyRepo;
import ru.bookingsystem.repository.UserRepo;
import ru.bookingsystem.requests.CompanyCreateRequest;
import ru.bookingsystem.requests.CompanyUpdateRequest;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Slf4j
@RestController()
@AllArgsConstructor
public class CompanyController {

    private final CompanyRepo companyRepo;
    private final ObjectMapper objectMapper;
    private final UserRepo userRepo;

    @GetMapping("/api/companies/all")
    public List<Company> getAll(){
        return companyRepo.findAll();
    }

    @PostMapping("/api/companies/create")
    public Company addCompany(@RequestBody CompanyCreateRequest request){
        Company company = new Company();
        company.setId(null);
        company.setName(request.getName());
        return companyRepo.save(company);
    }

    @GetMapping("/api/companies/getById")
    public Company getById(@RequestParam Long id){
        return companyRepo.findById(id).orElseThrow();
    }

    @PutMapping("/api/companies/editCompanyById")
    public String editById(@RequestBody CompanyUpdateRequest request){
        if(!companyRepo.existsById(request.getId())) {
            return "No such row";
        }
        Company company = new Company(
                request.getId(),
                request.getName()
        );
        return companyRepo.save(company).toString();
    }

    @DeleteMapping("api/companies/deleteById")
    public void deleteById(@RequestParam Long id){
        List<User> users = userRepo.findByCompanyId(id);
        for(User u : users){
            u.setCompany(null);
        }
        userRepo.saveAll(users);
        companyRepo.deleteById(id);
    }
}
