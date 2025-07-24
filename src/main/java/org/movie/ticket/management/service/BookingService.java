package org.movie.ticket.management.service;

import org.movie.ticket.management.dto.BookingRequestDTO;
import org.movie.ticket.management.dto.BookingResponseDTO;
import org.movie.ticket.management.entity.*;
import org.movie.ticket.management.exception.SeatNotAvailableException;
import org.movie.ticket.management.exception.SeatNotFoundException;
import org.movie.ticket.management.exception.ShowNotFoundException;
import org.movie.ticket.management.exception.UserNotFoundException;
import org.movie.ticket.management.repository.BookingRepository;
import org.movie.ticket.management.repository.SeatRepository;
import org.movie.ticket.management.repository.ShowRepository;
import org.movie.ticket.management.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookingService {
    public final BookingRepository bookingRepository;
    public final UserRepository userRepository;
    public final ShowRepository showRepository;
    public final SeatRepository seatRepository;

    private static final BigDecimal REGULAR_PRICE = BigDecimal.valueOf(200);
    private static final BigDecimal VIP_PRICE = BigDecimal.valueOf(400);

    public BookingService(BookingRepository bookingRepository, UserRepository userRepository, ShowRepository showRepository, SeatRepository seatRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.showRepository = showRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public String bookMovie(BookingRequestDTO bookingRequestDTO) {
        User user = userRepository.findById(bookingRequestDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User with ID: " + bookingRequestDTO.getUserId() + " not found"));

        Show show = showRepository.findById(bookingRequestDTO.getShowId())
                .orElseThrow(() -> new ShowNotFoundException("Show with ID: " + bookingRequestDTO.getShowId() + " not found"));

        // 1. Lock requested seats
        List<Long> requestedSeatIds = bookingRequestDTO.getSeatIds();
        List<Seat> lockedSeats = seatRepository.findAllByIdWithLock(requestedSeatIds);

        // 2. Validate availability
        for (Seat seat : lockedSeats) {
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new SeatNotAvailableException("Seat with ID " + seat.getSeatId() + " is already booked.");
            }
        }

        // 3. Mark seats as BOOKED
        for (Seat seat : lockedSeats) {
            seat.setStatus(SeatStatus.BOOKED);
        }
        seatRepository.saveAll(lockedSeats); // persist status changes

        // 4. Calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Seat seat : lockedSeats) {
            if (seat.getSeatType() == SeatType.VIP) {
                totalAmount = totalAmount.add(VIP_PRICE);
            } else {
                totalAmount = totalAmount.add(REGULAR_PRICE);
            }
        }

        // 5. Create Booking record
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShow(show);
        booking.setBookedSeats(lockedSeats);
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setTotalAmount(totalAmount);

        Booking savedBooking = bookingRepository.save(booking);

        // 6. Return confirmation
        return "Booking confirmed. ID: " + savedBooking.getBookingId() +
                " | Seats: " + lockedSeats.stream()
                .map(seat -> String.valueOf(seat.getSeatNumber()))
                .collect(Collectors.joining(", ")) +
                " | Total Amount: ₹" + totalAmount;
    }

    public List<BookingResponseDTO> getUserBookingHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID: " + userId + " not found"));

        List<Booking> bookings = bookingRepository.findByUser_UserId(userId);

        return bookings.stream()
                .map(booking -> new BookingResponseDTO(
                        booking.getBookingId(),
                        booking.getShow().getMovie().getTitle(),
                        booking.getShow().getShowTime(),
                        booking.getBookedSeats().stream()
                                .map(Seat::getSeatNumber)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
}
