package ru.bookingsystem.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bookingsystem.DTO.ResourceDTO;
import ru.bookingsystem.DTO.requests.ResourceCreateRequest;
import ru.bookingsystem.DTO.requests.ResourceUpdateRequest;
import ru.bookingsystem.entity.Company;
import ru.bookingsystem.entity.Resource;
import ru.bookingsystem.entity.ResourceType;
import ru.bookingsystem.exception.AlreadyExistsException;
import ru.bookingsystem.exception.NotFoundException;
import ru.bookingsystem.repository.CompanyRepo;
import ru.bookingsystem.repository.ResourceRepo;
import ru.bookingsystem.repository.ResourceTypeRepo;
import ru.bookingsystem.service.interfaces.ResourceService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final CompanyRepo companyRepo;
    private final ResourceRepo resourceRepo;
    private final ResourceTypeRepo resourceTypeRepo;

    @Override
    public ResourceDTO addResource(ResourceCreateRequest request) {

        if (request.getName() == null) throw new NullPointerException("name must be not null");

        Company company = companyRepo.findById(request.getCompanyId()).orElseThrow(() ->
                new NotFoundException("company with id " + request.getCompanyId() + " not found"));

        ResourceType resourceType = resourceTypeRepo.findById(request.getResourceTypeId()).orElseThrow(() ->
                new NotFoundException("resource with id " + request.getResourceTypeId() + " not found"));

        List<Resource> resources = resourceRepo.findAllByCompanyId(company.getId());

        for (Resource resource : resources){
            if (resource.getName().equals(request.getName()) && resource.getType().getId() == request.getResourceTypeId()) {
                throw new AlreadyExistsException("resource with name " + request.getName() +
                        " and same type already exists. You can change quantity");
            }
        }

        Resource resource = new Resource();
        resource.setCompany(company);
        resource.setName(request.getName());
        resource.setType(resourceType);
        resource.setDescription(request.getDescription());
        resource.setQuantity(request.getQuantity());

        return new ResourceDTO(resourceRepo.save(resource));
    }

    @Override
    public ResourceDTO findById(Long id) {
        return new ResourceDTO(resourceRepo.findById(id).orElseThrow(() ->
                new NotFoundException("Resource with id " + id + " not found")));
    }

    @Override
    public List<ResourceDTO> findAll() {
        return resourceRepo.findAll()
                .stream()
                .map(ResourceDTO::new)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        resourceRepo.deleteById(id);
    }

    @Override
    public ResourceDTO editResource(ResourceUpdateRequest request) {

        if (request.getId() == null) throw new NullPointerException("id must be not null");

        if (request.getName() == null) throw new NullPointerException("name must be not null");

        Resource resource = resourceRepo.findById(request.getId()).orElseThrow(()->
                new NotFoundException("resource with id " + request.getId() + " not found"));

        Company company = companyRepo.findById(request.getCompanyId()).orElseThrow(() ->
                new NotFoundException("company with id " + request.getCompanyId() + " not found"));

        ResourceType resourceType = resourceTypeRepo.findById(request.getResourceTypeId()).orElseThrow(() ->
                new NotFoundException("resource with id " + request.getResourceTypeId() + " not found"));

        resource.setCompany(company);
        resource.setName(request.getName());
        resource.setType(resourceType);
        resource.setDescription(request.getDescription());
        resource.setQuantity(request.getQuantity());

        return new ResourceDTO(resourceRepo.save(resource));
    }

    @Override
    public List<ResourceDTO> findAllByCompanyId(Long companyId) {

        Company company = companyRepo.findById(companyId).orElseThrow(() ->
                new NotFoundException("company with id " + companyId + " not found"));

        return resourceRepo.findAllByCompanyId(company.getId())
                .stream()
                .map(ResourceDTO::new)
                .toList();
    }
}
