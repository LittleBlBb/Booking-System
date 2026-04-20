package ru.bookingsystem.service.implementation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.bookingsystem.DTO.RegistrationUserDTO;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.entity.constant.Role;
import ru.bookingsystem.exception.AlreadyExistsException;
import ru.bookingsystem.repository.CompanyRepo;
import ru.bookingsystem.repository.UserRepo;
import ru.bookingsystem.DTO.requests.UserUpdateRequest;
import ru.bookingsystem.service.interfaces.UserService;
import ru.bookingsystem.util.CustomUserDetails;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final CompanyRepo companyRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User findById(Long id){

        return userRepo.findById(id).orElseThrow();
    }

    @Override
    public List<User> findAll(){

        return userRepo.findAll();
    }

    @Override
    public User addUser(RegistrationUserDTO request){

        if(!request.getPassword().equals(request.getConfirmPassword())){
            throw new IllegalStateException("Passwords are not equals");
        }

        if (findByUsername(request.getUsername()).isPresent()){

            throw new AlreadyExistsException("User " + request.getUsername() + " already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

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
                request.getPassword(),
                request.getRole()
        );

        return userRepo.save(user).toString();
    }

    @Override
    public Optional<User> findByUsername(String username) {

        return userRepo.findByUsername(username);
    }

    @Override
    @Transactional
    public CustomUserDetails loadUserByUsername(String username) {

        User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));

        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().getName())
        );

        return new CustomUserDetails(
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
