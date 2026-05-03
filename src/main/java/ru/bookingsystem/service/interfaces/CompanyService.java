package ru.bookingsystem.service.interfaces;

import org.springframework.security.core.Authentication;
import ru.bookingsystem.DTO.CompanyDTO;
import ru.bookingsystem.entity.Company;
import ru.bookingsystem.DTO.requests.CompanyCreateRequest;
import ru.bookingsystem.DTO.requests.CompanyUpdateRequest;

import java.util.List;

public interface CompanyService {

    CompanyDTO addCompany(Authentication authentication, CompanyCreateRequest request);

    List<CompanyDTO> findAll();

    Company findById(Long id);

    CompanyDTO editCompany(Authentication authentication, CompanyUpdateRequest request);

    void deleteById(Authentication authentication, Long id);
}
