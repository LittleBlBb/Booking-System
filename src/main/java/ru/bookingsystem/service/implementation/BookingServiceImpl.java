package ru.bookingsystem.service.implementation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bookingsystem.DTO.requests.BookingCreateRequest;
import ru.bookingsystem.DTO.requests.BookingUpdateRequest;
import ru.bookingsystem.entity.Booking;
import ru.bookingsystem.entity.Resource;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.entity.constant.BookingStatus;
import ru.bookingsystem.repository.BookingRepo;
import ru.bookingsystem.repository.ResourceRepo;
import ru.bookingsystem.repository.UserRepo;
import ru.bookingsystem.service.interfaces.BookingService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepo bookingRepo;
    private final UserRepo userRepo;
    private final ResourceRepo resourceRepo;

    @Transactional
    @Override
    public Booking editBooking(BookingUpdateRequest request){

        Booking booking = bookingRepo.findById(request.getBookingId()).orElseThrow();

        User user = userRepo.findById(request.getUserId()).orElseThrow();

        Resource resource = resourceRepo.findById(request.getResourceId()).orElseThrow();

        validateRequestByUserIdAndResourceIdAndDateTime(user, resource, request.getStartTime(), request.getEndTime());

        booking.setUser(user);
        booking.setResource(resource);
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setStatus(request.getStatus());

        return bookingRepo.save(booking);
    }

    @Transactional
    @Override
    public Booking addBooking(BookingCreateRequest request){

        User user = userRepo.findById(request.getUserId()).orElseThrow();

        Resource resource = resourceRepo.findById(request.getResourceId()).orElseThrow();

        validateRequestByUserIdAndResourceIdAndDateTime(user, resource, request.getStartTime(), request.getEndTime());

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setResource(resource);
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());

        return bookingRepo.save(booking);
    }

    @Override
    public Booking findById(Long id){

        return bookingRepo.findById(id).orElseThrow();
    }

    @Override
    public List<Booking> findAll(){

        return bookingRepo.findAll();
    }

    @Override
    public List<Booking> findAllByStatusAndUserId(BookingStatus status, Long userId){

        return bookingRepo.findAllByStatusAndUserId(status, userId);
    }

    @Override
    public void deleteById(Long id){

        bookingRepo.deleteById(id);
    }

    private void validateRequestByUserIdAndResourceIdAndDateTime(User user, Resource resource, LocalDateTime start, LocalDateTime end){

        if (user.getCompany() == null ||
                !user.getCompany().equals(resource.getCompany())) {
            throw new IllegalArgumentException("User and resource must belong to same company");
        }

        if (start.isAfter(end)){
            throw new IllegalArgumentException("Invalid time interval");
        }

        if (start.isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("Cannot book in the past");
        }

        long countOverlapping = bookingRepo.countOverlappingBookings(resource, start, end);

        if (countOverlapping >= resource.getQuantity()){
            throw new IllegalStateException("Resource is fully booked for this time");
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
}
