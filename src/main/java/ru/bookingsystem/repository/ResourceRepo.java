package ru.bookingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookingsystem.entity.Company;
import ru.bookingsystem.entity.Resource;

import java.util.List;

public interface ResourceRepo extends JpaRepository<Resource, Long> {

    List<Resource> findAllByCompany(Company company);
}
