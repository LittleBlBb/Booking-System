package ru.bookingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookingsystem.entity.ResourceType;

import java.util.List;

public interface ResourceTypeRepo extends JpaRepository<ResourceType, Long> {

    List<ResourceType> findAllByCompanyId(Long id);
}
