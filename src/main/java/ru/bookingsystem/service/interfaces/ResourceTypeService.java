package ru.bookingsystem.service.interfaces;

import ru.bookingsystem.DTO.requests.ResourceTypeCreateRequest;
import ru.bookingsystem.entity.ResourceType;

import java.util.List;

public interface ResourceTypeService {
    ResourceType findById(Long id);

    List<ResourceType> findAll();

    ResourceType addResourceType(ResourceTypeCreateRequest request);
}
