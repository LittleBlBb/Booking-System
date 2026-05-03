package ru.bookingsystem.service.implementation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.bookingsystem.DTO.CompanyDTO;
import ru.bookingsystem.DTO.requests.CompanyCreateRequest;
import ru.bookingsystem.DTO.requests.CompanyUpdateRequest;
import ru.bookingsystem.entity.*;
import ru.bookingsystem.entity.constant.Role;
import ru.bookingsystem.exception.AlreadyInCompanyException;
import ru.bookingsystem.exception.NotFoundException;
import ru.bookingsystem.exception.NotOwnerException;
import ru.bookingsystem.repository.BookingRepo;
import ru.bookingsystem.repository.CompanyRepo;
import ru.bookingsystem.repository.ResourceRepo;
import ru.bookingsystem.repository.UserRepo;
import ru.bookingsystem.service.interfaces.CompanyService;
import ru.bookingsystem.service.interfaces.ResourceService;
import ru.bookingsystem.service.interfaces.ResourceTypeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepo companyRepo;
    private final UserRepo userRepo;

    @Override
    @Transactional
    public CompanyDTO addCompany(Authentication authentication, CompanyCreateRequest request){

        String name = authentication.getName();
        User user = userRepo.findByUsername(name).orElseThrow();

        if (user.getCompany() != null) throw new AlreadyInCompanyException("user with id " + user.getId() + " already in company");

        Company company = createCompany(user, request.getName());

        user.setRole(Role.OWNER);
        userRepo.save(user);

        return new CompanyDTO(company);
    }

    private Company createCompany(User user, String companyName){

        Company company = new Company();
        company.setName(companyName);

        user.setCompany(company);

        return companyRepo.save(company);
    }

    @Override
    public List<CompanyDTO> findAll(){

        return companyRepo.findAll()
                .stream()
                .map(CompanyDTO::new)
                .toList();
    }

    @Override
    public Company findById(Long id) {

        return companyRepo.findById(id).orElseThrow();
    }

    @Override
    @Transactional
    public CompanyDTO editCompany(Authentication authentication, CompanyUpdateRequest request) {

        companyRepo.findById(request.getId()).orElseThrow(() ->
                new NotFoundException("company with id " + request.getId() + " not found"));

        User user = userRepo.findByUsername(authentication.getName()).orElseThrow();

        if (!user.getRole().equals(Role.OWNER) || !user.getCompany().getId().equals(request.getId())){
            throw new NotOwnerException();
        }

        Company company = new Company(
                request.getId(),
                request.getName()
        );

        return new CompanyDTO(companyRepo.save(company));
    }

    @Override
    @Transactional
    public void deleteById(Authentication authentication, Long id){

        User user = userRepo.findByUsername(authentication.getName())
                .orElseThrow();

        if (!user.getRole().equals(Role.OWNER) ||
                !user.getCompany().getId().equals(id)){
            throw new NotOwnerException();
        }

        List<User> users = userRepo.findByCompanyId(id);
        for (User u : users){
            u.setCompany(null);
            u.setRole(Role.USER);
        }
        userRepo.saveAll(users);

        companyRepo.deleteById(id);
    }
}
