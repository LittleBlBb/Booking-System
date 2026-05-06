package ru.bookingsystem.service.implementation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.bookingsystem.DTO.BookingDTO;
import ru.bookingsystem.DTO.requests.BookingCreateRequest;
import ru.bookingsystem.DTO.requests.BookingUpdateRequest;
import ru.bookingsystem.entity.Booking;
import ru.bookingsystem.entity.CompanySettings;
import ru.bookingsystem.entity.Resource;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.entity.constant.BookingStatus;
import ru.bookingsystem.entity.constant.Role;
import ru.bookingsystem.exception.AlreadyExistsException;
import ru.bookingsystem.exception.BookingException;
import ru.bookingsystem.exception.NoPermissionException;
import ru.bookingsystem.exception.NotFoundException;
import ru.bookingsystem.repository.BookingRepo;
import ru.bookingsystem.repository.ResourceRepo;
import ru.bookingsystem.repository.UserRepo;
import ru.bookingsystem.service.interfaces.BookingService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepo bookingRepo;
    private final ResourceRepo resourceRepo;
    private final UserRepo userRepo;

    @Transactional
    @Override
    public BookingDTO editBooking(Authentication authentication, BookingUpdateRequest request){

        Booking booking = bookingRepo.findById(request.getBookingId()).orElseThrow();

        User user = userRepo.findByUsername(authentication.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Resource resource = resourceRepo.findById(request.getResourceId()).orElseThrow();

        validateRequestByUserIdAndResourceIdAndDateTime(user, resource, request.getStartTime(), request.getEndTime());

        booking.setUser(user);
        booking.setResource(resource);
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setStatus(request.getStatus());

        return new BookingDTO(bookingRepo.save(booking));
    }

    @Transactional
    @Override
    public BookingDTO addBooking(Authentication authentication, BookingCreateRequest request){

        User user = userRepo.findByUsername(authentication.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Resource resource = resourceRepo.findById(request.getResourceId()).orElseThrow();

        validateRequestByUserIdAndResourceIdAndDateTime(user, resource, request.getStartTime(), request.getEndTime());

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setResource(resource);
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setStatus(BookingStatus.ACTIVE);

        return new BookingDTO(bookingRepo.save(booking));
    }

    @Override
    public BookingDTO findById(Long id){

        return new BookingDTO(bookingRepo.findById(id).orElseThrow());
    }

    @Override
    public List<BookingDTO> findAll(){

        return bookingRepo.findAll()
                .stream()
                .map(BookingDTO::new)
                .toList();
    }

    @Override
    public List<Booking> findAllByStatusAndUserId(BookingStatus status, Long userId){

        return bookingRepo.findAllByStatusAndUserId(status, userId);
    }

    @Override
    public void deleteById(Authentication authentication, Long id){

        User user = userRepo.findByUsername(authentication.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getRole() == Role.USER){

            Booking booking = bookingRepo.findById(id).orElseThrow(() ->
                    new NotFoundException("Booking with id " + id + " not found"));

            if (booking.getUser().getId() != user.getId()) throw new NoPermissionException();
        }

        bookingRepo.deleteById(id);
    }

    private void validateRequestByUserIdAndResourceIdAndDateTime(User user, Resource resource, LocalDateTime start, LocalDateTime end){

        if (user.getCompany() == null ||
                !user.getCompany().equals(resource.getCompany())) {
            throw new NoPermissionException("User and resource must belong to same company");
        }

        if (start.isAfter(end)){
            throw new BookingException("Invalid time interval");
        }

        if (start.isBefore(LocalDateTime.now())){
            throw new BookingException("Cannot book in the past");
        }

        long countOverlapping = bookingRepo.countOverlappingBookings(resource, start, end);

        if (countOverlapping >= resource.getQuantity()){
            throw new BookingException("Resource is fully booked for this time");
        }

        CompanySettings settings = resource.getCompany().getSettings();

        if (settings != null) {

            LocalTime startTime = start.toLocalTime();
            LocalTime endTime = end.toLocalTime();

            if (startTime.isBefore(settings.getWorkStart()) ||
                    endTime.isAfter(settings.getWorkEnd())) {
                throw new BookingException("Booking outside working hours");
            }

            long minutes = Duration.between(start, end).toMinutes();

            if (minutes > settings.getMaxBookingDurationMinutes()) {
                throw new BookingException("Booking duration exceeded");
            }

            long bookingCount = bookingRepo.countUserBookingsInInterval(user, start, end);

            if (bookingCount >= settings.getMaxBookingsPerUser()) {
                throw new BookingException("Too many bookings for this time");
            }
        }
    }

    @Override
    public void cancelAllBookingsByUserId(Long userId){

        List<Booking> bookings = findAllByStatusAndUserId(BookingStatus.ACTIVE, userId);

        for (Booking booking : bookings) {
            booking.setStatus(BookingStatus.CANCELED);
        }

        bookingRepo.saveAll(bookings);
    }

    @Override
    public List<BookingDTO> findAllByResourceId(Long resourceId) {

        return bookingRepo.findAllByResourceId(resourceId)
                .stream()
                .map(BookingDTO::new)
                .toList();
    }

    @Override
    public List<BookingDTO> findAllByResourceIdAndStatus(Long resourceId, BookingStatus status) {

        return bookingRepo.findAllByResourceIdAndStatus(resourceId, status)
                .stream()
                .map(BookingDTO::new)
                .toList();
    }

    @Override
    public List<BookingDTO> findAllByCompanyId(Long companyId){

        return bookingRepo.findAllByCompanyId(companyId)
                .stream()
                .map(BookingDTO::new)
                .toList();
    }

    @Override
    public List<BookingDTO> findAllByCompanyIdAndStatus(Long companyId, BookingStatus status) {
        return bookingRepo.findAllByCompanyIdAndStatus(companyId, status)
                .stream()
                .map(BookingDTO::new)
                .toList();
    }

    @Override
    public Page<BookingDTO> findAllByUserId(Long userId, int page, int size, String[] sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        return bookingRepo.findAllByUserId(userId, pageable)
                .map(BookingDTO::new);
    }

    @Override
    public Page<BookingDTO> findAllByUserIdAndStatus(Long userId, int page, int size, String[] sort, BookingStatus status) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        return bookingRepo.findAllByUserIdAndStatus(userId, pageable, status)
                .map(BookingDTO::new);
    }

    @Override
    public BookingDTO cancelById(Authentication authentication, Long id) {

        User user = userRepo.findByUsername(authentication.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Booking booking = bookingRepo.findById(id).orElseThrow(() ->
                new NotFoundException("Booking with id " + id + " not found"));

        if (!booking.getStatus().equals(BookingStatus.ACTIVE)){
            throw new AlreadyExistsException("Booking already canceled or expired");
        }

        if (user.getCompany().getId() != booking.getResource().getCompany().getId()){
            throw new NoPermissionException();
        } else {
            if (user.getId() != booking.getUser().getId() && user.getRole().equals(Role.USER)){
                throw new NoPermissionException();
            }
        }

        booking.setStatus(BookingStatus.CANCELED);

        return new BookingDTO(bookingRepo.save(booking));
    }
}
