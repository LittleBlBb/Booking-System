package ru.bookingsystem.service.interfaces;

import ru.bookingsystem.DTO.requests.ResourceCreateRequest;
import ru.bookingsystem.DTO.requests.ResourceUpdateRequest;
import ru.bookingsystem.entity.Resource;

import java.util.List;

public interface ResourceService {
    Resource addResource(ResourceCreateRequest request);

    Resource findById(Long id);

    List<Resource> findAll();

    void deleteById(Long id);

    Resource editResource(ResourceUpdateRequest request);

    List<Resource> findAllByCompanyId(Long companyId);
}
