package org.movie.ticket.management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.movie.ticket.management.dto.BookingRequestDTO;
import org.movie.ticket.management.dto.BookingResponseDTO;
import org.movie.ticket.management.entity.Booking;
import org.movie.ticket.management.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Booking Details", description = "Manage Movie bookings.")
public class BookingController {
    public final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(summary = "Book a movie")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<String> bookMovie(@RequestBody BookingRequestDTO bookingRequestDTO) {
        String response = bookingService.bookMovie(bookingRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get User's booking history")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<List<BookingResponseDTO>> getUserBookings(@PathVariable Long userId) {
        List<BookingResponseDTO> bookingHistory = bookingService.getUserBookingHistory(userId);
        return ResponseEntity.ok(bookingHistory);
    }
}
