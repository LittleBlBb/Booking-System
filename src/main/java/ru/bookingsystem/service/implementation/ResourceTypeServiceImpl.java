package ru.bookingsystem.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bookingsystem.DTO.requests.ResourceTypeCreateRequest;
import ru.bookingsystem.entity.Company;
import ru.bookingsystem.entity.ResourceType;
import ru.bookingsystem.exception.NotFoundException;
import ru.bookingsystem.repository.ResourceTypeRepo;
import ru.bookingsystem.service.interfaces.CompanyService;
import ru.bookingsystem.service.interfaces.ResourceTypeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceTypeServiceImpl implements ResourceTypeService {

    private final ResourceTypeRepo resourceTypeRepo;
    private final CompanyService companyService;

    @Override
    public ResourceType findById(Long id) {
        return resourceTypeRepo.findById(id).orElseThrow(() -> new NotFoundException("ResourceType with id " + id + " not found"));
    }

    @Override
    public List<ResourceType> findAll(){

        return resourceTypeRepo.findAll();
    }

    @Override
    public ResourceType addResourceType(ResourceTypeCreateRequest request) {

        ResourceType resourceType = new ResourceType();

        Company company = companyService.findById(request.getCompanyId());

        if (company == null) throw new NotFoundException("Company with id " + request.getCompanyId() + " not found");

        resourceType.setName(request.getName());
        resourceType.setCompany(company);

        return resourceTypeRepo.save(resourceType);
    }
}
