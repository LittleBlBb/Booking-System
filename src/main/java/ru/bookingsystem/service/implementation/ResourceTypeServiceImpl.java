package ru.bookingsystem.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bookingsystem.DTO.ResourceTypeDTO;
import ru.bookingsystem.DTO.requests.ResourceTypeCreateRequest;
import ru.bookingsystem.entity.Company;
import ru.bookingsystem.entity.ResourceType;
import ru.bookingsystem.exception.NotFoundException;
import ru.bookingsystem.repository.CompanyRepo;
import ru.bookingsystem.repository.ResourceTypeRepo;
import ru.bookingsystem.service.interfaces.ResourceTypeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceTypeServiceImpl implements ResourceTypeService {

    private final ResourceTypeRepo resourceTypeRepo;
    private final CompanyRepo companyRepo;

    @Override
    public ResourceTypeDTO findById(Long id) {
        ResourceType resourceType = resourceTypeRepo.findById(id).orElseThrow(() ->
                new NotFoundException("ResourceType with id " + id + " not found"));

        return new ResourceTypeDTO(resourceType);
    }

    @Override
    public List<ResourceTypeDTO> findAll(){
        return resourceTypeRepo.findAll()
                .stream()
                .map(ResourceTypeDTO::new)
                .toList();
    }

    @Override
    public ResourceTypeDTO addResourceType(ResourceTypeCreateRequest request) {

        ResourceType resourceType = new ResourceType();

        Company company = companyRepo.findById(request.getCompanyId()).orElseThrow(() ->
                new NotFoundException("Company with id " + request.getCompanyId() + " not found"));

        resourceType.setName(request.getName());
        resourceType.setCompany(company);

        return new ResourceTypeDTO(resourceTypeRepo.save(resourceType));
    }

    @Override
    public List<ResourceTypeDTO> findAllByCompanyId(Long id) {
        return resourceTypeRepo.findAllByCompanyId(id)
                .stream()
                .map(ResourceTypeDTO::new)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        resourceTypeRepo.deleteById(id);
    }
}
