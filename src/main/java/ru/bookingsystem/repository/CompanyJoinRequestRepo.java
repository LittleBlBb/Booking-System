package ru.bookingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookingsystem.entity.Company;
import ru.bookingsystem.entity.CompanyJoinRequest;
import ru.bookingsystem.entity.User;

import java.util.List;

public interface CompanyJoinRequestRepo extends JpaRepository<CompanyJoinRequest, Long> {

    List<CompanyJoinRequest> findAllByCompany(Company company);

    List<CompanyJoinRequest> findByUserAndCompany(User user, Company company);
}
