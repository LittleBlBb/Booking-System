package ru.bookingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookingsystem.entity.Company;
import ru.bookingsystem.entity.ResourceType;

import java.util.Optional;

public interface ResourceTypeRepo extends JpaRepository<ResourceType, Long> {

    Optional<ResourceType> findByNameAndCompany(String name, Company company);
}
