package ru.bookingsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.DTO.BookingDTO;
import ru.bookingsystem.DTO.requests.BookingCreateRequest;
import ru.bookingsystem.DTO.requests.BookingUpdateRequest;
import ru.bookingsystem.entity.constant.BookingStatus;
import ru.bookingsystem.service.interfaces.BookingService;

import java.util.List;

@Tag(name = "booking_methods", description = "operations with booking")
@AllArgsConstructor
@RestController
@RequestMapping("/api/bookings")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {
    private final BookingService bookingService;

    @Operation(
            summary = "find all bookings",
            description = "returns all bookings DTO"
    )
    @GetMapping("/all")
    public List<BookingDTO> findAll() {

        return bookingService.findAll();
    }

    @Operation(
            summary = "add booking to database",
            description = "creating new booking by request and save to database in service"

    )
    @PostMapping("/create")
    public BookingDTO addBooking(Authentication authentication, @RequestBody BookingCreateRequest request) {

        return bookingService.addBooking(authentication, request);
    }

    @Operation(
            summary = "find booking by id",
            description = "returns booking DTO with selected id"
    )
    @GetMapping("/getById")
    public BookingDTO findById(@RequestParam Long id) {

        return bookingService.findById(id);
    }

    @Operation(
            summary = "edit booking",
            description = "editing booking by id, returns new company DTO"
    )
    @PutMapping("/editBookingById")
    public BookingDTO editBooking(Authentication authentication, @RequestBody BookingUpdateRequest request) {

        return bookingService.editBooking(authentication, request);
    }

    @Operation(
            summary = "delete booking",
            description = "deleting booking by id in service, returns void"
    )
    @DeleteMapping("/deleteById")
    public void deleteById(Authentication authentication, @RequestParam Long id) {

        bookingService.deleteById(authentication, id);
    }

    @GetMapping("/{resourceId}/bookings")
    public List<BookingDTO> findAllByResourceId(@PathVariable Long resourceId,
                                                @RequestParam(required = false) BookingStatus status){
        return status == null
                ? bookingService.findAllByResourceId(resourceId)
                : bookingService.findAllByResourceIdAndStatus(resourceId, status);
    }

    @GetMapping("/{companyId}/all")
    public List<BookingDTO> findAllByCompanyId(@PathVariable Long companyId,
                                               @RequestParam(required = false) BookingStatus status){

        return status == null
                ? bookingService.findAllByCompanyId(companyId)
                : bookingService.findAllByCompanyIdAndStatus(companyId, status);
    }
}