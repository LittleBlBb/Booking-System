package ru.bookingsystem.service.implementation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.bookingsystem.DTO.UserActivationResponse;
import ru.bookingsystem.DTO.UserResponseDTO;
import ru.bookingsystem.DTO.requests.RoleUpdateRequest;
import ru.bookingsystem.DTO.requests.UserUpdateRequest;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.entity.constant.Role;
import ru.bookingsystem.exception.NoPermissionException;
import ru.bookingsystem.exception.NotFoundException;
import ru.bookingsystem.repository.UserRepo;
import ru.bookingsystem.service.interfaces.BookingService;
import ru.bookingsystem.service.interfaces.CompanyService;
import ru.bookingsystem.service.interfaces.UserService;
import ru.bookingsystem.util.CustomUserDetails;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final CompanyService companyService;
    private final UserCleanupService userCleanupService;

    @Override
    public UserResponseDTO findById(Long id){

        return new UserResponseDTO(userRepo.findById(id).orElseThrow());
    }

    @Override
    public List<UserResponseDTO> findAll(){

        return userRepo.findAll()
                .stream()
                .map(UserResponseDTO::new)
                .toList();
    }

    @Override
    public void delete(Authentication authentication){

        User user = findByUsername(authentication.getName());

        userRepo.deleteById(user.getId());
    }

    @Override
    public UserResponseDTO updateUser(Authentication authentication, UserUpdateRequest request){

        User user = findByUsername(authentication.getName());

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())){
            throw new NoPermissionException("invalid password");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())){
            throw new NoPermissionException("the passwords don't match");
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return new UserResponseDTO(userRepo.save(user));
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
    public UserResponseDTO updateRole(RoleUpdateRequest request) {

        User user = userRepo.findById(request.getUserId()).orElseThrow(() ->
                new NotFoundException("User with id " + request.getUserId() + " notfound"));

        user.setRole(request.getRole());

        return new UserResponseDTO(userRepo.save(user));
    }

    @Override
    public UserResponseDTO deleteUserFromCompany(Long id) {

        User user = userRepo.findById(id).orElseThrow(() ->
                new NotFoundException("User with id " + id + " notfound"));

        user.setCompany(null);

        return new UserResponseDTO(userRepo.save(user));
    }

    @Override
    public UserResponseDTO save(User user) {
        return new UserResponseDTO(userRepo.save(user));
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

    @Transactional
    @Override
    public UserResponseDTO leaveCompany(Authentication authentication) {

        User user = findByUsername(authentication.getName());

        if (user.getRole().equals(Role.OWNER)){
            User admin = userRepo.findFirstByCompanyIdAndRole(user.getCompany().getId(), Role.ADMIN);

            if (admin == null) {
                admin = userRepo.findFirstByCompanyIdAndRole(user.getCompany().getId(), Role.USER);
            }

            if (admin == null) {
                companyService.deleteById(authentication, user.getCompany().getId());
            } else {
                admin.setRole(Role.OWNER);
                userRepo.save(admin);
            }
        }

        user.setRole(Role.USER);
        user.setCompany(null);

        userCleanupService.handleUserLeaving(user.getId());

        return new UserResponseDTO(userRepo.save(user));
    }
}
