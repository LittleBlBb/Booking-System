package ru.bookingsystem.service.interfaces;

import ru.bookingsystem.entity.Company;
import ru.bookingsystem.requests.CompanyCreateRequest;
import ru.bookingsystem.requests.CompanyUpdateRequest;

import java.util.List;

public interface CompanyService {

    Company addCompany(CompanyCreateRequest request);

    List<Company> findAll();

    Company findById(Long id);

    String editCompany(CompanyUpdateRequest request);

    void deleteById(Long id);
}
