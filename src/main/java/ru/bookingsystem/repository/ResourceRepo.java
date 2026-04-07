package ru.bookingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookingsystem.entity.Resource;

public interface ResourceRepo extends JpaRepository<Resource, Long> {
}
