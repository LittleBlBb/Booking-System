package ru.bookingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookingsystem.entity.Company;

public interface CompanyRepo extends JpaRepository<Company, Long> {


}
