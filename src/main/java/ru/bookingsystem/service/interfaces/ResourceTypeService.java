package ru.bookingsystem.service.interfaces;

import org.springframework.security.core.Authentication;
import ru.bookingsystem.DTO.ResourceTypeDTO;
import ru.bookingsystem.DTO.requests.ResourceTypeCreateRequest;

import java.util.List;

public interface ResourceTypeService {
    ResourceTypeDTO findById(Long id);

    List<ResourceTypeDTO> findAll(Authentication authentication);

    ResourceTypeDTO addResourceType(ResourceTypeCreateRequest request);

    List<ResourceTypeDTO> findAllByCompanyId(Long id);

    void deleteById(Long id);

    ResourceTypeDTO updateById(Authentication authentication, ResourceTypeDTO request);
}
