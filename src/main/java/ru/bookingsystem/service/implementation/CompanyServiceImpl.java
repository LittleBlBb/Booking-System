package ru.bookingsystem.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bookingsystem.entity.Company;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.repository.CompanyRepo;
import ru.bookingsystem.repository.UserRepo;
import ru.bookingsystem.requests.CompanyCreateRequest;
import ru.bookingsystem.requests.CompanyUpdateRequest;
import ru.bookingsystem.service.interfaces.CompanyService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepo companyRepo;
    private final UserRepo userRepo;

    @Override
    public Company addCompany(CompanyCreateRequest request){

        Company company = new Company();
        company.setId(null);
        company.setName(request.getName());

        return companyRepo.save(company);
    }

    @Override
    public List<Company> findAll(){

        return companyRepo.findAll();
    }

    @Override
    public Company findById(Long id) {

        return companyRepo.findById(id).orElseThrow();
    }

    @Override
    public String editCompany(CompanyUpdateRequest request){

        if(!companyRepo.existsById(request.getId())) {
            return "No such row";
        }
        Company company = new Company(
                request.getId(),
                request.getName()
        );

        return companyRepo.save(company).toString();
    }

    @Override
    public void deleteById(Long id){

        List<User> users = userRepo.findByCompanyId(id);
        for(User u : users){
            u.setCompany(null);
        }
        userRepo.saveAll(users);
        companyRepo.deleteById(id);
    }
}
