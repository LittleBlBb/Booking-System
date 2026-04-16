package ru.bookingsystem.service.implementation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.bookingsystem.DTO.RegistrationUserDTO;
import ru.bookingsystem.DTO.UserDTO;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.entity.constant.Role;
import ru.bookingsystem.exception.AppError;
import ru.bookingsystem.repository.CompanyRepo;
import ru.bookingsystem.repository.UserRepo;
import ru.bookingsystem.DTO.requests.UserUpdateRequest;
import ru.bookingsystem.service.interfaces.UserService;

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
    public ResponseEntity<?> addUser(RegistrationUserDTO request){

        if(!request.getPassword().equals(request.getConfirmPassword())){
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Passwords are not equals"), HttpStatus.BAD_REQUEST);
        }

        if (findByUsername(request.getUsername()).isPresent()){
            return new ResponseEntity<>(new AppError(
                    HttpStatus.BAD_REQUEST.value(),
                    String.format("User with username '%s' already exists", request.getUsername())
            ),
                    HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

//        if (request.getCompanyId() != null){
//            Company company = new Company();
//            company.setId(request.getCompanyId());
//            user.setCompany(company);
//        }

        userRepo.save(user);

        return ResponseEntity.ok(new UserDTO(user.getId(), user.getCompany(), user.getEmail(), user.getUsername()));
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

//    private void validateUser(User user){
//
//        if (findByUsername(user.getUsername()) != null){
//            throw new UsernameAlreadyUsedException();
//        }
//        if (findByEmail(user.getEmail) != null){
//            throw new EmailAlreadyUsedException();
//        }
//    }

    @Override
    public Optional<User> findByUsername(String username){

        return userRepo.findByUsername(username);
//
//
//                .orElseThrow(() -> new UsernameNotFoundException(
//                String.format("User '%s' not found", username)
//        ));
    }

//    @Override
//    public User findByEmail(String email){
//
//        return userRepo.findByEmail(email).orElseThrow(() -> new EmailNotFoundException(
//                String.format("User with '%s' not found", email)
//        ));
//    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = findByUsername(username).orElseThrow();

        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().getName())
        );

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
