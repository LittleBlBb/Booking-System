package ru.bookingsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.bookingsystem.DTO.AuthResponse;
import ru.bookingsystem.DTO.RegistrationUserDTO;
import ru.bookingsystem.DTO.UserResponseDTO;
import ru.bookingsystem.DTO.requests.AuthRequest;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.entity.constant.Role;
import ru.bookingsystem.exception.AlreadyExistsException;
import ru.bookingsystem.exception.UserNotActivatedException;
import ru.bookingsystem.service.implementation.AuthServiceImpl;
import ru.bookingsystem.service.implementation.MailSenderServiceImpl;
import ru.bookingsystem.service.interfaces.UserService;
import ru.bookingsystem.util.CustomUserDetails;
import ru.bookingsystem.util.JwtUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private JwtUtils jwtUtils;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserService userService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private MailSenderServiceImpl mailSenderService;

    @InjectMocks
    private AuthServiceImpl authService;

    // ─────────────────────────────────────────────
    //  LOGIN
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("login()")
    class LoginTests {

        private AuthRequest request;
        private User activeUser;
        private CustomUserDetails userDetails;

        @BeforeEach
        void setUp() {
            request = new AuthRequest();
            request.setLogin("john");
            request.setPassword("secret");

            activeUser = new User();
            activeUser.setUsername("john");
            activeUser.setEmail("john@example.com");
            activeUser.setActive(true);

            userDetails = new CustomUserDetails(
                    "john", "john@example.com", "encoded", List.of()
            );
        }

        @Test
        @DisplayName("Successful login — returns token and user data")
        void login_success_returnsAuthResponse() {
            when(userService.findByUsername("john")).thenReturn(activeUser);
            when(userService.loadUserByUsername("john")).thenReturn(userDetails);
            when(jwtUtils.generateToken(userDetails)).thenReturn("jwt-token");

            AuthResponse response = authService.login(request);

            assertThat(response.getToken()).isEqualTo("jwt-token");
            assertThat(response.getUsername()).isEqualTo("john");
            assertThat(response.getEmail()).isEqualTo("john@example.com");
        }

        @Test
        @DisplayName("Inactive user — throws UserNotActivatedException")
        void login_inactiveUser_throwsUserNotActivatedException() {
            activeUser.setActive(false);
            when(userService.findByUsername("john")).thenReturn(activeUser);

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(UserNotActivatedException.class)
                    .hasMessageContaining("check your email");

            // JWT не должен генерироваться для неактивного пользователя
            verifyNoInteractions(jwtUtils);
        }

        @Test
        @DisplayName("Wrong password — AuthenticationManager throws BadCredentialsException")
        void login_wrongPassword_propagatesException() {
            doThrow(new BadCredentialsException("Bad credentials"))
                    .when(authenticationManager)
                    .authenticate(any(UsernamePasswordAuthenticationToken.class));

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BadCredentialsException.class);

            // До проверки активности и генерации токена не доходим
            verifyNoInteractions(userService, jwtUtils);
        }

        @Test
        @DisplayName("AuthenticationManager is called with correct credentials")
        void login_passesCorrectCredentialsToAuthManager() {
            when(userService.findByUsername("john")).thenReturn(activeUser);
            when(userService.loadUserByUsername("john")).thenReturn(userDetails);
            when(jwtUtils.generateToken(any())).thenReturn("token");

            authService.login(request);

            ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                    ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
            verify(authenticationManager).authenticate(captor.capture());

            assertThat(captor.getValue().getPrincipal()).isEqualTo("john");
            assertThat(captor.getValue().getCredentials()).isEqualTo("secret");
        }
    }

    // ─────────────────────────────────────────────
    //  REGISTRATION
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("registration()")
    class RegistrationTests {

        private RegistrationUserDTO validRequest;

        @BeforeEach
        void setUp() {
            validRequest = new RegistrationUserDTO();
            validRequest.setUsername("newuser");
            validRequest.setEmail("new@example.com");
            validRequest.setPassword("pass123");
            validRequest.setConfirmPassword("pass123");
        }

        private void stubSuccessfulRegistration() {
            when(userService.existsByUsername(anyString())).thenReturn(false);
            when(userService.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encoded-pass");

            User saved = new User();
            saved.setUsername("newuser");
            saved.setEmail("new@example.com");
            when(userService.save(any(User.class))).thenReturn(new UserResponseDTO(saved));
        }

        @Test
        @DisplayName("Passwords do not match — throws IllegalStateException")
        void registration_passwordMismatch_throwsIllegalStateException() {
            validRequest.setConfirmPassword("different");
            assertThatThrownBy(() -> authService.registration(validRequest))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Passwords are not equals");

            verifyNoInteractions(userService, mailSenderService);
        }

        @Test
        @DisplayName("Username already exists — throws AlreadyExistsException")
        void registration_duplicateUsername_throwsAlreadyExistsException() {
            when(userService.existsByUsername("newuser")).thenReturn(true);

            assertThatThrownBy(() -> authService.registration(validRequest))
                    .isInstanceOf(AlreadyExistsException.class)
                    .hasMessageContaining("newuser");
        }

        @Test
        @DisplayName("Email == null — throws NullPointerException")
        void registration_nullEmail_throwsNullPointerException() {
            validRequest.setEmail(null);
            when(userService.existsByUsername(anyString())).thenReturn(false);

            assertThatThrownBy(() -> authService.registration(validRequest))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Email required");
        }

        @Test
        @DisplayName("Email already in use — throws AlreadyExistsException")
        void registration_duplicateEmail_throwsAlreadyExistsException() {
            when(userService.existsByUsername(anyString())).thenReturn(false);
            when(userService.existsByEmail("new@example.com")).thenReturn(true);

            assertThatThrownBy(() -> authService.registration(validRequest))
                    .isInstanceOf(AlreadyExistsException.class)
                    .hasMessageContaining("new@example.com");
        }

        @Test
        @DisplayName("New user is created inactive with USER role")
        void registration_success_userCreatedInactiveWithUserRole() {
            stubSuccessfulRegistration();

            authService.registration(validRequest);

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userService).save(captor.capture());

            assertThat(captor.getValue().getActive()).isFalse();
            assertThat(captor.getValue().getRole()).isEqualTo(Role.USER);
        }

        @Test
        @DisplayName("Password is stored encoded, not in plain text")
        void registration_success_passwordIsEncoded() {
            when(userService.existsByUsername(anyString())).thenReturn(false);
            when(userService.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode("pass123")).thenReturn("$2a$bcrypt-hash");

            User saved = new User();
            saved.setUsername("newuser");
            saved.setEmail("new@example.com");
            when(userService.save(any(User.class))).thenReturn(new UserResponseDTO(saved));

            authService.registration(validRequest);

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userService).save(captor.capture());

            assertThat(captor.getValue().getPassword()).isEqualTo("$2a$bcrypt-hash");
            assertThat(captor.getValue().getPassword()).isNotEqualTo("pass123");
        }

        @Test
        @DisplayName("Activation code is generated and the same one is sent via email")
        void registration_success_activationCodeGeneratedAndEmailSent() {
            stubSuccessfulRegistration();

            authService.registration(validRequest);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userService).save(userCaptor.capture());
            String activationCode = userCaptor.getValue().getActivationCode();

            assertThat(activationCode).isNotBlank();

            verify(mailSenderService).sendActivationCode(
                    eq("new@example.com"),
                    eq("newuser"),
                    eq(activationCode)
            );
        }

        @Test
        @DisplayName("On duplicate username — email check is not reached")
        void registration_duplicateUsername_emailCheckNotReached() {
            when(userService.existsByUsername("newuser")).thenReturn(true);

            assertThatThrownBy(() -> authService.registration(validRequest))
                    .isInstanceOf(AlreadyExistsException.class);

            verify(userService, never()).existsByEmail(anyString());
        }

        @Test
        @DisplayName("On password mismatch — username check is not reached")
        void registration_passwordMismatch_usernameCheckNotReached() {
            validRequest.setConfirmPassword("wrong");

            assertThatThrownBy(() -> authService.registration(validRequest))
                    .isInstanceOf(IllegalStateException.class);

            verify(userService, never()).existsByUsername(anyString());
        }
    }
}