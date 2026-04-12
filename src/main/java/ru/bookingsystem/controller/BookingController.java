package ru.bookingsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.DTO.requests.BookingCreateRequest;
import ru.bookingsystem.DTO.requests.BookingUpdateRequest;
import ru.bookingsystem.entity.Booking;
import ru.bookingsystem.service.interfaces.BookingService;

import java.util.List;
@Tag(name = "booking_methods", description = "operations with booking")
@RestController()
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @Operation(
            summary = "find all bookings",
            description = "returns all bookings DTO"
    )
    @GetMapping("/api/bookings/all")
    public List<Booking> findAll() {

        return bookingService.findAll();
    }

    @Operation(
            summary = "add booking to database",
            description = "creating new booking by request and save to database in service"

    )
    @PostMapping("/api/bookings/create")
    public Booking addBooking(@RequestBody BookingCreateRequest request) { // Company -> Booking

        return bookingService.addBooking(request);
    }

    @Operation(
            summary = "find booking by id",
            description = "returns booking DTO with selected id"
    )
    @GetMapping("/api/bookings/getById")
    public Booking findById(@RequestParam Long id) {

        return bookingService.findById(id);
    }

    @Operation(
            summary = "edit booking",
            description = "editing booking by id, returns new company DTO"
    )
    @PutMapping("/api/bookings/editCompanyById")
    public Booking editBooking(@RequestBody BookingUpdateRequest request) { // Company -> Booking

        return bookingService.editBooking(request);
    }

    @Operation(
            summary = "delete booking",
            description = "deleting booking by id in service, returns void"
    )
    @DeleteMapping("api/bookings/deleteById")
    public void deleteById(@RequestParam Long id) {

        bookingService.deleteById(id);
    }
}