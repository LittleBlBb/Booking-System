package ru.bookingsystem.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.entity.Company;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.repository.CompanyRepo;
import ru.bookingsystem.repository.UserRepo;
import ru.bookingsystem.requests.UserCreateRequest;
import ru.bookingsystem.requests.UserUpdateRequest;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Slf4j
@RestController()
@RequiredArgsConstructor
public class UserController {

    private final UserRepo userRepo;
    private final CompanyRepo companyRepo;
    private final ObjectMapper objectMapper;

    @GetMapping("/api/getById")
    public User getUserById(@RequestParam Long userId){
        return userRepo.findById(userId).orElseThrow();
    }

    @GetMapping("/api/allUsers")
    public List<User> getAll(){
        return userRepo.findAll();
    }

    @PostMapping("/api/addUser")
    public User addUser(@RequestBody UserCreateRequest request){

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

    @DeleteMapping("/api/deleteById")
    public void deleteUser(@RequestParam Long userId){
        userRepo.deleteById(userId);
    }

    @PutMapping("/api/editUserById")
    public String editUser(@RequestBody UserUpdateRequest request){
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
