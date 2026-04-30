package ru.bookingsystem.service.implementation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import ru.bookingsystem.DTO.UserActivationResponse;
import ru.bookingsystem.DTO.requests.RoleUpdateRequest;
import ru.bookingsystem.DTO.requests.UserUpdateRequest;
import ru.bookingsystem.entity.Company;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.exception.NotFoundException;
import ru.bookingsystem.repository.CompanyRepo;
import ru.bookingsystem.repository.UserRepo;
import ru.bookingsystem.service.interfaces.UserService;
import ru.bookingsystem.util.CustomUserDetails;

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
    public void deleteById(Long userId){

        userRepo.deleteById(userId);
    }

    @Override
    public User updateUser(UserUpdateRequest request){

        User user = userRepo.findById(request.getId()).orElseThrow(() ->
                new NotFoundException("User with id " + request.getId() + " not found"));

        Company company = companyRepo.findById(request.getCompanyId()).orElseThrow(() ->
                new NotFoundException("Company with id " + request.getCompanyId() + " not found"));

        user.setCompany(company);
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        return userRepo.save(user);
    }

    @Override
    public User findByUsername(String username) {

        return userRepo.findByUsername(username).orElseThrow(() ->
                new NotFoundException("user " + username + " not found"));
    }

    @Override
    public Boolean existsByUsername(String username){

        return userRepo.existsByUsername(username);
    }

    @Override
    @Transactional
    public CustomUserDetails loadUserByUsername(String username) {

        User user = findByUsername(username);

        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        return new CustomUserDetails(
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    @Override
    public UserActivationResponse activateUser(String code){
        User user = userRepo.findByActivationCode(code);

        if (user == null){
            return new UserActivationResponse("Activation code is not found");
        }

        user.setActivationCode(null);
        user.setActive(true);
        userRepo.save(user);

        return new UserActivationResponse("User successfully activated");
    }

    @Override
    public User updateRole(RoleUpdateRequest request) {

        User user = userRepo.findById(request.getUserId()).orElseThrow(() -> new NotFoundException("User with id " + request.getUserId() + " notfound"));

        user.setRole(request.getRole());

        return userRepo.save(user);
    }

    @Override
    public User deleteUserFromCompany(Long id) {

        User user = userRepo.findById(id).orElseThrow(() -> new NotFoundException("User with id " + id + " notfound"));

        user.setCompany(null);

        return userRepo.save(user);
    }

    @Override
    public User save(User user) {
        return userRepo.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow(() ->
                new NotFoundException("User with email " + email + " not found"));
    }

    @Override
    public Boolean existsByEmail(String email){

        return userRepo.existsByEmail(email);
    }
}
