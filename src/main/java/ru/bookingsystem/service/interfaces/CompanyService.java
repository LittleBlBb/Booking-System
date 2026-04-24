package ru.bookingsystem.service.interfaces;

import org.springframework.security.core.Authentication;
import ru.bookingsystem.entity.Company;
import ru.bookingsystem.DTO.requests.CompanyCreateRequest;
import ru.bookingsystem.DTO.requests.CompanyUpdateRequest;

import java.util.List;

public interface CompanyService {

    Company addCompany(Authentication authentication, CompanyCreateRequest request);

    List<Company> findAll();

    Company findById(Long id);

    Company editCompany(Authentication authentication, CompanyUpdateRequest request);

    void deleteById(Authentication authentication, Long id);
}
