package ru.bookingsystem.service.interfaces;

import ru.bookingsystem.DTO.ResourceDTO;
import ru.bookingsystem.DTO.requests.ResourceCreateRequest;
import ru.bookingsystem.DTO.requests.ResourceUpdateRequest;

import java.util.List;

public interface ResourceService {
    ResourceDTO addResource(ResourceCreateRequest request);

    ResourceDTO findById(Long id);

    List<ResourceDTO> findAll();

    void deleteById(Long id);

    ResourceDTO editResource(ResourceUpdateRequest request);

    List<ResourceDTO> findAllByCompanyId(Long companyId);
}
