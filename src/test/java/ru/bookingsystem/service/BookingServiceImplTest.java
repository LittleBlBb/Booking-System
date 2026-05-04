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
import org.springframework.security.core.Authentication;
import ru.bookingsystem.DTO.BookingDTO;
import ru.bookingsystem.DTO.requests.BookingCreateRequest;
import ru.bookingsystem.entity.*;
import ru.bookingsystem.entity.constant.BookingStatus;
import ru.bookingsystem.entity.constant.Role;
import ru.bookingsystem.exception.BookingException;
import ru.bookingsystem.exception.NoPermissionException;
import ru.bookingsystem.repository.BookingRepo;
import ru.bookingsystem.repository.ResourceRepo;
import ru.bookingsystem.repository.UserRepo;
import ru.bookingsystem.service.implementation.BookingServiceImpl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock private BookingRepo bookingRepo;
    @Mock private ResourceRepo resourceRepo;
    @Mock private UserRepo userRepo;
    @Mock private Authentication authentication;

    @InjectMocks
    private BookingServiceImpl bookingService;


    private Company company;
    private User user;
    private Resource resource;
    private BookingCreateRequest createRequest;

    private final LocalDateTime START = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
    private final LocalDateTime END   = LocalDateTime.now().plusDays(1).withHour(11).withMinute(0);

    @BeforeEach
    void setUp() {
        company = new Company();
        company.setId(1L);

        user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setCompany(company);

        resource = new Resource();
        resource.setId(1L);
        resource.setCompany(company);
        resource.setQuantity(5);

        createRequest = new BookingCreateRequest();
        createRequest.setResourceId(1L);
        createRequest.setStartTime(START);
        createRequest.setEndTime(END);

        lenient().when(authentication.getName()).thenReturn("john");
        lenient().when(userRepo.findByUsername("john")).thenReturn(Optional.of(user));
        lenient().when(resourceRepo.findById(1L)).thenReturn(Optional.of(resource));
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  VALIDATE: компания
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Validation: same company ownership")
    class CompanyValidationTests {

        @Test
        @DisplayName("User without company — throws NoPermissionException")
        void addBooking_userWithoutCompany_throwsIllegalArgumentException() {
            user.setCompany(null);

            assertThatThrownBy(() -> bookingService.addBooking(authentication, createRequest))
                    .isInstanceOf(NoPermissionException.class)
                    .hasMessageContaining("same company");
        }

        @Test
        @DisplayName("Resource from another company — throws NoPermissionException")
        void addBooking_resourceFromDifferentCompany_throwsIllegalArgumentException() {
            Company otherCompany = new Company();
            otherCompany.setId(99L);
            resource.setCompany(otherCompany);

            assertThatThrownBy(() -> bookingService.addBooking(authentication, createRequest))
                    .isInstanceOf(NoPermissionException.class)
                    .hasMessageContaining("same company");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  VALIDATE: временной интервал
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Validation: time interval")
    class TimeIntervalValidationTests {

        @Test
        @DisplayName("Start is after end — throws BookingException")
        void addBooking_startAfterEnd_throwsIllegalArgumentException() {
            createRequest.setStartTime(END);
            createRequest.setEndTime(START);

            assertThatThrownBy(() -> bookingService.addBooking(authentication, createRequest))
                    .isInstanceOf(BookingException.class)
                    .hasMessageContaining("Invalid time interval");
        }

        @Test
        @DisplayName("Booking in the past — throws BookingException")
        void addBooking_startInPast_throwsIllegalArgumentException() {
            createRequest.setStartTime(LocalDateTime.now().minusHours(2));
            createRequest.setEndTime(LocalDateTime.now().minusHours(1));

            assertThatThrownBy(() -> bookingService.addBooking(authentication, createRequest))
                    .isInstanceOf(BookingException.class)
                    .hasMessageContaining("past");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  VALIDATE: перекрытие и количество ресурсов
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Validation: resource availability")
    class ResourceAvailabilityTests {

        @Test
        @DisplayName("All slots are booked (overlapping >= quantity) — throws BookingException")
        void addBooking_allSlotsBooked_throwsIllegalStateException() {
            resource.setQuantity(3);
            when(bookingRepo.countOverlappingBookings(resource, START, END)).thenReturn(3L);

            assertThatThrownBy(() -> bookingService.addBooking(authentication, createRequest))
                    .isInstanceOf(BookingException.class)
                    .hasMessageContaining("fully booked");
        }

        @Test
        @DisplayName("One slot remaining — booking succeeds")
        void addBooking_oneSlotRemaining_success() {
            resource.setQuantity(3);
            when(bookingRepo.countOverlappingBookings(resource, START, END)).thenReturn(2L);

            Booking saved = new Booking();
            saved.setUser(user);
            saved.setResource(resource);
            saved.setStartTime(START);
            saved.setEndTime(END);
            saved.setStatus(BookingStatus.ACTIVE);
            when(bookingRepo.save(any(Booking.class))).thenReturn(saved);

            BookingDTO result = bookingService.addBooking(authentication, createRequest);

            assertThat(result).isNotNull();
            verify(bookingRepo).save(any(Booking.class));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  VALIDATE: настройки компании (CompanySettings)
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Validation: company settings")
    class CompanySettingsValidationTests {

        private CompanySettings settings;

        @BeforeEach
        void setUpSettings() {
            settings = new CompanySettings();
            settings.setWorkStart(LocalTime.of(9, 0));
            settings.setWorkEnd(LocalTime.of(18, 0));
            settings.setMaxBookingDurationMinutes(120);
            settings.setMaxBookingsPerUser(3);
            company.setSettings(settings);

            lenient().when(bookingRepo.countOverlappingBookings(resource, START, END)).thenReturn(0L);
        }

        @Test
        @DisplayName("Booking starts before working hours — throws BookingException")
        void addBooking_startBeforeWorkStart_throwsIllegalStateException() {
            createRequest.setStartTime(START.withHour(7));
            createRequest.setEndTime(START.withHour(8));

            when(bookingRepo.countOverlappingBookings(any(), any(), any())).thenReturn(0L);

            assertThatThrownBy(() -> bookingService.addBooking(authentication, createRequest))
                    .isInstanceOf(BookingException.class)
                    .hasMessageContaining("working hours");
        }

        @Test
        @DisplayName("Booking ends after working hours — throws BookingException")
        void addBooking_endAfterWorkEnd_throwsIllegalStateException() {
            createRequest.setStartTime(START.withHour(17));
            createRequest.setEndTime(START.withHour(19));
            when(bookingRepo.countOverlappingBookings(any(), any(), any())).thenReturn(0L);

            assertThatThrownBy(() -> bookingService.addBooking(authentication, createRequest))
                    .isInstanceOf(BookingException.class)
                    .hasMessageContaining("working hours");
        }

        @Test
        @DisplayName("Booking duration exceeds maximum — throws BookingException")
        void addBooking_durationExceedsMax_throwsIllegalStateException() {
            settings.setMaxBookingDurationMinutes(30);

            when(bookingRepo.countOverlappingBookings(any(), any(), any())).thenReturn(0L);

            assertThatThrownBy(() -> bookingService.addBooking(authentication, createRequest))
                    .isInstanceOf(BookingException.class)
                    .hasMessageContaining("duration exceeded");
        }

        @Test
        @DisplayName("User booking limit exceeded — throws BookingException")
        void addBooking_userBookingLimitExceeded_throwsIllegalStateException() {
            settings.setMaxBookingsPerUser(2);
            when(bookingRepo.countUserBookingsInInterval(user, START, END)).thenReturn(2L);

            assertThatThrownBy(() -> bookingService.addBooking(authentication, createRequest))
                    .isInstanceOf(BookingException.class)
                    .hasMessageContaining("Too many bookings");
        }

        @Test
        @DisplayName("At booking limit boundary — succeeds")
        void addBooking_userBookingAtLimit_success() {
            settings.setMaxBookingsPerUser(3);
            when(bookingRepo.countUserBookingsInInterval(user, START, END)).thenReturn(2L);

            Booking saved = new Booking();
            saved.setUser(user);
            saved.setResource(resource);
            saved.setStartTime(START);
            saved.setEndTime(END);
            saved.setStatus(BookingStatus.ACTIVE);
            when(bookingRepo.save(any(Booking.class))).thenReturn(saved);

            assertThat(bookingService.addBooking(authentication, createRequest)).isNotNull();
        }

        @Test
        @DisplayName("No company settings (settings == null) — restrictions are not applied")
        void addBooking_noCompanySettings_noSettingsValidation() {
            company.setSettings(null);

            Booking saved = new Booking();
            saved.setUser(user);
            saved.setResource(resource);
            saved.setStartTime(START);
            saved.setEndTime(END);
            saved.setStatus(BookingStatus.ACTIVE);
            when(bookingRepo.save(any(Booking.class))).thenReturn(saved);

            assertThat(bookingService.addBooking(authentication, createRequest)).isNotNull();
            verify(bookingRepo, never()).countUserBookingsInInterval(any(), any(), any());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  VALIDATE: удаление
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("deleteById(): access control")
    class DeleteBookingTests {

        private Booking booking;

        @BeforeEach
        void setUp() {
            booking = new Booking();
            booking.setId(10L);
            booking.setUser(user);
        }

        @Test
        @DisplayName("USER deletes another user's booking — throws NoPermissionException")
        void deleteById_userDeletesOthersBooking_throwsNoPermissionException() {
            user.setRole(Role.USER);

            User otherUser = new User();
            otherUser.setId(99L);
            booking.setUser(otherUser);

            when(bookingRepo.findById(10L)).thenReturn(Optional.of(booking));

            assertThatThrownBy(() -> bookingService.deleteById(authentication, 10L))
                    .isInstanceOf(NoPermissionException.class);

            verify(bookingRepo, never()).deleteById(any());
        }

        @Test
        @DisplayName("USER deletes own booking — succeeds")
        void deleteById_userDeletesOwnBooking_success() {
            user.setRole(Role.USER);
            booking.setUser(user);

            when(bookingRepo.findById(10L)).thenReturn(Optional.of(booking));

            bookingService.deleteById(authentication, 10L);

            verify(bookingRepo).deleteById(10L);
        }

        @Test
        @DisplayName("ADMIN deletes any booking without ownership check")
        void deleteById_adminDeletesAnyBooking_success() {
            user.setRole(Role.ADMIN);

            bookingService.deleteById(authentication, 10L);

            verify(bookingRepo, never()).findById(any());
            verify(bookingRepo).deleteById(10L);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  cancelAllBookingsByUserId
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("cancelAllBookingsByUserId()")
    class CancelAllBookingsTests {

        @Test
        @DisplayName("All active bookings are set to CANCELED")
        void cancelAll_setsStatusCanceledForAllActiveBookings() {
            Booking b1 = new Booking(); b1.setStatus(BookingStatus.ACTIVE);
            Booking b2 = new Booking(); b2.setStatus(BookingStatus.ACTIVE);

            when(bookingRepo.findAllByStatusAndUserId(BookingStatus.ACTIVE, 1L))
                    .thenReturn(List.of(b1, b2));

            bookingService.cancelAllBookingsByUserId(1L);

            assertThat(b1.getStatus()).isEqualTo(BookingStatus.CANCELED);
            assertThat(b2.getStatus()).isEqualTo(BookingStatus.CANCELED);

            ArgumentCaptor<List<Booking>> captor = ArgumentCaptor.forClass(List.class);
            verify(bookingRepo).saveAll(captor.capture());
            assertThat(captor.getValue()).containsExactly(b1, b2);
        }

        @Test
        @DisplayName("No active bookings — saveAll called with empty list")
        void cancelAll_noActiveBookings_saveAllWithEmptyList() {
            when(bookingRepo.findAllByStatusAndUserId(BookingStatus.ACTIVE, 1L))
                    .thenReturn(List.of());

            bookingService.cancelAllBookingsByUserId(1L);

            ArgumentCaptor<List<Booking>> captor = ArgumentCaptor.forClass(List.class);
            verify(bookingRepo).saveAll(captor.capture());
            assertThat(captor.getValue()).isEmpty();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  addBooking: корректность сохраняемой сущности
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("addBooking(): saved Booking correctness")
    class AddBookingEntityTests {

        @Test
        @DisplayName("New booking is always created with ACTIVE status")
        void addBooking_newBookingAlwaysHasActiveStatus() {
            when(bookingRepo.countOverlappingBookings(resource, START, END)).thenReturn(0L);

            Booking saved = new Booking();
            saved.setUser(user);
            saved.setResource(resource);
            saved.setStartTime(START);
            saved.setEndTime(END);
            saved.setStatus(BookingStatus.ACTIVE);
            when(bookingRepo.save(any(Booking.class))).thenReturn(saved);

            bookingService.addBooking(authentication, createRequest);

            ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
            verify(bookingRepo).save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo(BookingStatus.ACTIVE);
        }
    }
}