package ru.bookingsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.bookingsystem.DTO.UserActivationResponse;
import ru.bookingsystem.DTO.requests.UserUpdateRequest;
import ru.bookingsystem.entity.Company;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.entity.constant.Role;
import ru.bookingsystem.exception.NoPermissionException;
import ru.bookingsystem.exception.NotFoundException;
import ru.bookingsystem.repository.UserRepo;
import ru.bookingsystem.service.implementation.UserCleanupService;
import ru.bookingsystem.service.implementation.UserServiceImpl;
import ru.bookingsystem.service.interfaces.CompanyService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepo userRepo;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private CompanyService companyService;
    @Mock private UserCleanupService userCleanupService;
    @Mock private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    // ─────────────────────────────────────────────────────────────────────────
    //  updateUser
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("updateUser()")
    class UpdateUserTests {

        private User user;
        private UserUpdateRequest request;

        @BeforeEach
        void setUp() {
            user = new User();
            user.setUsername("john");
            user.setPassword("encoded-old");

            request = new UserUpdateRequest();
            request.setUsername("john_new");
            request.setEmail("new@example.com");
            request.setCurrentPassword("old-pass");
            request.setPassword("new-pass");
            request.setConfirmPassword("new-pass");

            lenient().when(authentication.getName()).thenReturn("john");
            lenient().when(userRepo.findByUsername("john")).thenReturn(Optional.of(user));
        }

        @Test
        @DisplayName("Wrong current password — throws NoPermissionException")
        void updateUser_wrongCurrentPassword_throwsNoPermissionException() {
            when(passwordEncoder.matches("old-pass", "encoded-old")).thenReturn(false);

            assertThatThrownBy(() -> userService.updateUser(authentication, request))
                    .isInstanceOf(NoPermissionException.class)
                    .hasMessageContaining("invalid password");

            verify(userRepo, never()).save(any());
        }

        @Test
        @DisplayName("New passwords do not match — throws NoPermissionException")
        void updateUser_passwordMismatch_throwsNoPermissionException() {
            when(passwordEncoder.matches("old-pass", "encoded-old")).thenReturn(true);
            request.setConfirmPassword("different");

            assertThatThrownBy(() -> userService.updateUser(authentication, request))
                    .isInstanceOf(NoPermissionException.class)
                    .hasMessageContaining("don't match");

            verify(userRepo, never()).save(any());
        }

        @Test
        @DisplayName("Successful update — data is saved with encoded password")
        void updateUser_success_savesEncodedPassword() {
            when(passwordEncoder.matches("old-pass", "encoded-old")).thenReturn(true);
            when(passwordEncoder.encode("new-pass")).thenReturn("encoded-new");
            when(userRepo.save(user)).thenReturn(user);

            userService.updateUser(authentication, request);

            verify(userRepo).save(user);
            assertThat(user.getPassword()).isEqualTo("encoded-new");
            assertThat(user.getUsername()).isEqualTo("john_new");
            assertThat(user.getEmail()).isEqualTo("new@example.com");
        }

        @Test
        @DisplayName("Password validation happens before match check — validation order")
        void updateUser_wrongPasswordCheckedBeforeMatch() {
            when(passwordEncoder.matches("old-pass", "encoded-old")).thenReturn(false);
            request.setConfirmPassword("different");

            assertThatThrownBy(() -> userService.updateUser(authentication, request))
                    .isInstanceOf(NoPermissionException.class)
                    .hasMessageContaining("invalid password");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  activateUser
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("activateUser()")
    class ActivateUserTests {

        @Test
        @DisplayName("Activation code not found — returns error message, user is not saved")
        void activateUser_codeNotFound_returnsErrorMessage() {
            when(userRepo.findByActivationCode("bad-code")).thenReturn(null);

            UserActivationResponse response = userService.activateUser("bad-code");

            assertThat(response.getMessage()).isEqualTo("Activation code is not found");
            verify(userRepo, never()).save(any());
        }

        @Test
        @DisplayName("Valid code — user becomes active, activation code is cleared")
        void activateUser_validCode_activatesUserAndClearsCode() {
            User user = new User();
            user.setActive(false);
            user.setActivationCode("valid-code");
            when(userRepo.findByActivationCode("valid-code")).thenReturn(user);
            when(userRepo.save(user)).thenReturn(user);

            UserActivationResponse response = userService.activateUser("valid-code");

            assertThat(response.getMessage()).isEqualTo("User successfully activated");
            assertThat(user.getActive()).isTrue();
            assertThat(user.getActivationCode()).isNull();
            verify(userRepo).save(user);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  leaveCompany
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("leaveCompany()")
    class LeaveCompanyTests {

        private Company company;
        private User owner;

        @BeforeEach
        void setUp() {
            company = new Company();
            company.setId(1L);

            owner = new User();
            owner.setId(1L);
            owner.setUsername("owner");
            owner.setRole(Role.OWNER);
            owner.setCompany(company);

            lenient().when(authentication.getName()).thenReturn("owner");
            lenient().when(userRepo.findByUsername("owner")).thenReturn(Optional.of(owner));
        }

        @Test
        @DisplayName("OWNER leaves, ADMIN exists — ADMIN becomes OWNER")
        void leaveCompany_ownerLeaves_adminBecomesOwner() {
            User admin = new User();
            admin.setId(2L);
            admin.setRole(Role.ADMIN);
            admin.setCompany(company);

            when(userRepo.findFirstByCompanyIdAndRole(1L, Role.ADMIN)).thenReturn(admin);
            when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0)); // два вызова save — any() покрывает оба

            userService.leaveCompany(authentication);

            assertThat(admin.getRole()).isEqualTo(Role.OWNER);
        }

        @Test
        @DisplayName("OWNER leaves, no ADMIN, USER exists — USER becomes OWNER")
        void leaveCompany_ownerLeaves_noAdmin_userBecomesOwner() {
            User regularUser = new User();
            regularUser.setId(3L);
            regularUser.setRole(Role.USER);
            regularUser.setCompany(company);

            when(userRepo.findFirstByCompanyIdAndRole(1L, Role.ADMIN)).thenReturn(null);
            when(userRepo.findFirstByCompanyIdAndRole(1L, Role.USER)).thenReturn(regularUser);
            when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            userService.leaveCompany(authentication);

            assertThat(regularUser.getRole()).isEqualTo(Role.OWNER);
        }

        @Test
        @DisplayName("OWNER leaves, no other users — company is deleted")
        void leaveCompany_ownerLeaves_noOtherUsers_companyDeleted() {
            when(userRepo.findFirstByCompanyIdAndRole(1L, Role.ADMIN)).thenReturn(null);
            when(userRepo.findFirstByCompanyIdAndRole(1L, Role.USER)).thenReturn(null);
            when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            userService.leaveCompany(authentication);

            verify(companyService).deleteById(authentication, 1L);
        }

        @Test
        @DisplayName("OWNER after leaving loses role and company")
        void leaveCompany_ownerAfterLeave_hasNoCompanyAndUserRole() {
            User admin = new User();
            admin.setId(2L);
            admin.setRole(Role.ADMIN);
            when(userRepo.findFirstByCompanyIdAndRole(1L, Role.ADMIN)).thenReturn(admin);
            when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            userService.leaveCompany(authentication);

            assertThat(owner.getRole()).isEqualTo(Role.USER);
            assertThat(owner.getCompany()).isNull();
        }

        @Test
        @DisplayName("Regular USER leaves — just loses company, cleanupService is called")
        void leaveCompany_regularUser_leavesCompanyAndCleanupCalled() {
            User regularUser = new User();
            regularUser.setId(5L);
            regularUser.setUsername("regular");
            regularUser.setRole(Role.USER);
            regularUser.setCompany(company);

            when(authentication.getName()).thenReturn("regular");
            when(userRepo.findByUsername("regular")).thenReturn(Optional.of(regularUser));
            when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            userService.leaveCompany(authentication);

            assertThat(regularUser.getCompany()).isNull();
            assertThat(regularUser.getRole()).isEqualTo(Role.USER);
            verify(userRepo, never()).findFirstByCompanyIdAndRole(any(), any());
            verify(userCleanupService).handleUserLeaving(5L);
        }

        @Test
        @DisplayName("After leaving, cleanup is called with user ID")
        void leaveCompany_cleanupCalledWithCorrectUserId() {
            User admin = new User();
            admin.setId(2L);
            admin.setRole(Role.ADMIN);
            when(userRepo.findFirstByCompanyIdAndRole(1L, Role.ADMIN)).thenReturn(admin);
            when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            userService.leaveCompany(authentication);

            verify(userCleanupService).handleUserLeaving(1L);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  loadUserByUsername
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("loadUserByUsername()")
    class LoadUserByUsernameTests {

        @Test
        @DisplayName("Role USER — authority ROLE_USER")
        void loadUserByUsername_userRole_hasCorrectAuthority() {
            User user = new User();
            user.setUsername("john");
            user.setRole(Role.USER);
            when(userRepo.findByUsername("john")).thenReturn(Optional.of(user));

            var details = userService.loadUserByUsername("john");

            assertThat(details.getAuthorities())
                    .extracting(a -> a.getAuthority())
                    .containsExactly("ROLE_USER");
        }

        @Test
        @DisplayName("Role OWNER — authority ROLE_OWNER")
        void loadUserByUsername_ownerRole_hasCorrectAuthority() {
            User user = new User();
            user.setUsername("boss");
            user.setRole(Role.OWNER);
            when(userRepo.findByUsername("boss")).thenReturn(Optional.of(user));

            var details = userService.loadUserByUsername("boss");

            assertThat(details.getAuthorities())
                    .extracting(a -> a.getAuthority())
                    .containsExactly("ROLE_OWNER");
        }

        @Test
        @DisplayName("User not found — throws NotFoundException")
        void loadUserByUsername_notFound_throwsNotFoundException() {
            when(userRepo.findByUsername("ghost")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.loadUserByUsername("ghost"))
                    .isInstanceOf(NotFoundException.class);
        }
    }
}