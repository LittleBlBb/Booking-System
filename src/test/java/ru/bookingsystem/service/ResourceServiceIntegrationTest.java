package ru.bookingsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.bookingsystem.DTO.ResourceDTO;
import ru.bookingsystem.DTO.requests.ResourceCreateRequest;
import ru.bookingsystem.DTO.requests.ResourceUpdateRequest;
import ru.bookingsystem.entity.Company;
import ru.bookingsystem.entity.ResourceType;
import ru.bookingsystem.exception.AlreadyExistsException;
import ru.bookingsystem.exception.NotFoundException;
import ru.bookingsystem.repository.CompanyRepo;
import ru.bookingsystem.repository.ResourceRepo;
import ru.bookingsystem.repository.ResourceTypeRepo;
import ru.bookingsystem.service.implementation.ResourceServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ResourceServiceIntegrationTest {

    @Autowired private ResourceServiceImpl resourceService;
    @Autowired private CompanyRepo companyRepo;
    @Autowired private ResourceTypeRepo resourceTypeRepo;
    @Autowired private ResourceRepo resourceRepo;

    private Company company;
    private ResourceType resourceType;

    @BeforeEach
    void setUp() {
        company = new Company(null, "Test Company");
        company = companyRepo.save(company);

        resourceType = new ResourceType();
        resourceType.setName("Meeting Room");
        resourceType.setCompany(company);
        resourceType = resourceTypeRepo.save(resourceType);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  addResource
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("addResource()")
    class AddResourceTests {

        @Test
        @DisplayName("Valid request — resource is saved and returned with correct fields")
        void addResource_validRequest_savedAndReturned() {
            ResourceCreateRequest request = buildCreateRequest("Room A", 3);

            ResourceDTO result = resourceService.addResource(request);

            assertThat(result.getId()).isNotNull();
            assertThat(result.getName()).isEqualTo("Room A");
            assertThat(result.getQuantity()).isEqualTo(3);
            assertThat(result.getCompany_id()).isEqualTo(company.getId());
            assertThat(result.getType_id()).isEqualTo(resourceType.getId());
        }

        @Test
        @DisplayName("Null name — NullPointerException before hitting the database")
        void addResource_nullName_throwsNullPointerException() {
            ResourceCreateRequest request = buildCreateRequest(null, 1);

            assertThatThrownBy(() -> resourceService.addResource(request))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("name must be not null");

            assertThat(resourceRepo.findAllByCompanyId(company.getId())).isEmpty();
        }

        @Test
        @DisplayName("Non-existent company — NotFoundException")
        void addResource_nonExistentCompany_throwsNotFoundException() {
            ResourceCreateRequest request = buildCreateRequest("Room B", 1);
            request.setCompanyId(99999L);

            assertThatThrownBy(() -> resourceService.addResource(request))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("99999");
        }

        @Test
        @DisplayName("Non-existent resource type — NotFoundException")
        void addResource_nonExistentResourceType_throwsNotFoundException() {
            ResourceCreateRequest request = buildCreateRequest("Room C", 1);
            request.setResourceTypeId(99999L);

            assertThatThrownBy(() -> resourceService.addResource(request))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("99999");
        }

        @Test
        @DisplayName("Duplicate name and type in same company — AlreadyExistsException")
        void addResource_duplicateNameAndType_throwsAlreadyExistsException() {
            resourceService.addResource(buildCreateRequest("Room A", 1));

            assertThatThrownBy(() -> resourceService.addResource(buildCreateRequest("Room A", 1)))
                    .isInstanceOf(AlreadyExistsException.class)
                    .hasMessageContaining("Room A");
        }

        @Test
        @DisplayName("Same name but different type — allowed, both resources are saved")
        void addResource_sameNameDifferentType_bothSaved() {
            ResourceType anotherType = new ResourceType();
            anotherType.setName("Desk");
            anotherType.setCompany(company);
            anotherType = resourceTypeRepo.save(anotherType);

            resourceService.addResource(buildCreateRequest("Room A", 1));

            ResourceCreateRequest request2 = buildCreateRequest("Room A", 1);
            request2.setResourceTypeId(anotherType.getId());
            resourceService.addResource(request2);

            assertThat(resourceRepo.findAllByCompanyId(company.getId())).hasSize(2);
        }

        @Test
        @DisplayName("Same name and type in different companies — allowed")
        void addResource_sameNameAndTypeInDifferentCompanies_bothSaved() {
            Company otherCompany = companyRepo.save(new Company(null, "Other Company"));
            ResourceType otherType = new ResourceType();
            otherType.setName("Meeting Room");
            otherType.setCompany(otherCompany);
            otherType = resourceTypeRepo.save(otherType);

            resourceService.addResource(buildCreateRequest("Room A", 1));

            ResourceCreateRequest request2 = new ResourceCreateRequest();
            request2.setName("Room A");
            request2.setQuantity(1);
            request2.setCompanyId(otherCompany.getId());
            request2.setResourceTypeId(otherType.getId());
            resourceService.addResource(request2);

            assertThat(resourceRepo.findAll()).hasSize(2);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  editResource
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("editResource()")
    class EditResourceTests {

        @Test
        @DisplayName("Valid update — fields are persisted correctly")
        void editResource_validRequest_fieldsUpdated() {
            ResourceDTO created = resourceService.addResource(buildCreateRequest("Old Name", 1));

            ResourceUpdateRequest update = new ResourceUpdateRequest();
            update.setId(created.getId());
            update.setName("New Name");
            update.setDescription("Updated description");
            update.setQuantity(5);
            update.setCompanyId(company.getId());
            update.setResourceTypeId(resourceType.getId());

            ResourceDTO result = resourceService.editResource(update);

            assertThat(result.getName()).isEqualTo("New Name");
            assertThat(result.getDescription()).isEqualTo("Updated description");
            assertThat(result.getQuantity()).isEqualTo(5);
        }

        @Test
        @DisplayName("Null id — NullPointerException")
        void editResource_nullId_throwsNullPointerException() {
            ResourceUpdateRequest update = new ResourceUpdateRequest();
            update.setId(null);
            update.setName("Name");
            update.setCompanyId(company.getId());
            update.setResourceTypeId(resourceType.getId());

            assertThatThrownBy(() -> resourceService.editResource(update))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("id must be not null");
        }

        @Test
        @DisplayName("Null name — NullPointerException")
        void editResource_nullName_throwsNullPointerException() {
            ResourceDTO created = resourceService.addResource(buildCreateRequest("Room", 1));

            ResourceUpdateRequest update = new ResourceUpdateRequest();
            update.setId(created.getId());
            update.setName(null);
            update.setCompanyId(company.getId());
            update.setResourceTypeId(resourceType.getId());

            assertThatThrownBy(() -> resourceService.editResource(update))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("name must be not null");
        }

        @Test
        @DisplayName("Non-existent resource id — NotFoundException")
        void editResource_nonExistentId_throwsNotFoundException() {
            ResourceUpdateRequest update = new ResourceUpdateRequest();
            update.setId(99999L);
            update.setName("Name");
            update.setCompanyId(company.getId());
            update.setResourceTypeId(resourceType.getId());

            assertThatThrownBy(() -> resourceService.editResource(update))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("99999");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  findAllByCompanyId
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("findAllByCompanyId()")
    class FindAllByCompanyIdTests {

        @Test
        @DisplayName("Returns only resources belonging to the requested company")
        void findAllByCompanyId_returnsOnlyCompanyResources() {
            Company otherCompany = companyRepo.save(new Company(null, "Other Company"));
            ResourceType otherType = new ResourceType();
            otherType.setName("Desk");
            otherType.setCompany(otherCompany);
            otherType = resourceTypeRepo.save(otherType);

            resourceService.addResource(buildCreateRequest("Room A", 1));
            resourceService.addResource(buildCreateRequest("Room B", 2));

            ResourceCreateRequest otherRequest = new ResourceCreateRequest();
            otherRequest.setName("Desk 1");
            otherRequest.setQuantity(1);
            otherRequest.setCompanyId(otherCompany.getId());
            otherRequest.setResourceTypeId(otherType.getId());
            resourceService.addResource(otherRequest);

            var results = resourceService.findAllByCompanyId(company.getId());

            assertThat(results).hasSize(2);
            assertThat(results).extracting(ResourceDTO::getName)
                    .containsExactlyInAnyOrder("Room A", "Room B");
        }

        @Test
        @DisplayName("Non-existent company — NotFoundException")
        void findAllByCompanyId_nonExistentCompany_throwsNotFoundException() {
            assertThatThrownBy(() -> resourceService.findAllByCompanyId(99999L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("99999");
        }

        @Test
        @DisplayName("Company with no resources — returns empty list")
        void findAllByCompanyId_noResources_returnsEmptyList() {
            var results = resourceService.findAllByCompanyId(company.getId());
            assertThat(results).isEmpty();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Helper
    // ─────────────────────────────────────────────────────────────────────────
    private ResourceCreateRequest buildCreateRequest(String name, int quantity) {
        ResourceCreateRequest request = new ResourceCreateRequest();
        request.setName(name);
        request.setQuantity(quantity);
        request.setCompanyId(company.getId());
        request.setResourceTypeId(resourceType.getId());
        return request;
    }
}