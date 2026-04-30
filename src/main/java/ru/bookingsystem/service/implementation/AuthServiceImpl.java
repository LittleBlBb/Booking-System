package ru.bookingsystem.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.bookingsystem.DTO.AuthResponse;
import ru.bookingsystem.DTO.RegistrationUserDTO;
import ru.bookingsystem.DTO.requests.AuthRequest;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.entity.constant.Role;
import ru.bookingsystem.exception.AlreadyExistsException;
import ru.bookingsystem.exception.UserNotActivatedException;
import ru.bookingsystem.service.interfaces.AuthService;
import ru.bookingsystem.service.interfaces.UserService;
import ru.bookingsystem.util.CustomUserDetails;
import ru.bookingsystem.util.JwtUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final MailSenderServiceImpl mailSenderService;

    @Override
    public AuthResponse login(AuthRequest request){

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));

        User user = userService.findByUsername(request.getLogin());

        if (!user.getActive()) throw new UserNotActivatedException("Please check your email to active account");

        CustomUserDetails userDetails = (CustomUserDetails) userService.loadUserByUsername(request.getLogin());

        String token = jwtUtils.generateToken(userDetails);

        return new AuthResponse(userDetails.getUsername(), userDetails.getEmail(), token);
    }

    @Override
    public User registration(RegistrationUserDTO request){

        if (!request.getPassword().equals(request.getConfirmPassword())){
            throw new IllegalStateException("Passwords are not equals");
        }

        if (userService.existsByUsername(request.getUsername())){
            throw new AlreadyExistsException("User " + request.getUsername() + " already exists");
        }

        if (request.getEmail() == null){
            throw new NullPointerException("Email required");
        }

        if (userService.existsByEmail(request.getEmail())){
            throw new AlreadyExistsException("Email " + request.getEmail() + " already in use");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setActive(false);
        user.setActivationCode(UUID.randomUUID().toString());

        mailSenderService.sendActivationCode(user.getEmail(), user.getUsername(), user.getActivationCode());

        return userService.save(user);
    }
}
