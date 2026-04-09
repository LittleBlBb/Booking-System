package ru.bookingsystem.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bookingsystem.entity.Company;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.repository.CompanyRepo;
import ru.bookingsystem.repository.UserRepo;
import ru.bookingsystem.requests.UserCreateRequest;
import ru.bookingsystem.requests.UserUpdateRequest;
import ru.bookingsystem.service.interfaces.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final CompanyRepo companyRepo;

    @Override
    public User findById(Long id){

        return userRepo.findById(id).orElseThrow();
    }

    @Override
    public List<User> findAll(){

        return userRepo.findAll();
    }

    @Override
    public User addUser(UserCreateRequest request){

        User user = new User();
        user.setUsername(request.getUsername());
        user.setRole(request.getRole());

        if (request.getCompanyId() != null){
            Company company = new Company();
            company.setId(request.getCompanyId());
            user.setCompany(company);
        }

        return userRepo.save(user);
    }

    @Override
    public void deleteById(Long userId){

        userRepo.deleteById(userId);
    }

    @Override
    public String updateUser(UserUpdateRequest request){

        if (!userRepo.existsById(request.getId())){
            return "No such row";
        }
        User user = new User(
                request.getId(),
                request.getCompanyId() != null ? companyRepo.findById(request.getCompanyId()).orElseThrow() : null,
                request.getUsername(),
                request.getRole()
        );

        return userRepo.save(user).toString();
    }
}
