package org.movie.ticket.management.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.movie.ticket.management.dto.BookingRequestDTO;
import org.movie.ticket.management.dto.BookingResponseDTO;
import org.movie.ticket.management.entity.*;
import org.movie.ticket.management.exception.SeatNotAvailableException;
import org.movie.ticket.management.exception.ShowNotFoundException;
import org.movie.ticket.management.exception.UserNotFoundException;
import org.movie.ticket.management.repository.BookingRepository;
import org.movie.ticket.management.repository.SeatRepository;
import org.movie.ticket.management.repository.ShowRepository;
import org.movie.ticket.management.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock private UserRepository userRepository;
    @Mock private ShowRepository showRepository;
    @Mock private SeatRepository seatRepository;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBookMovie_Success() {
        Long userId = 1L;
        Long showId = 10L;
        List<Long> seatIds = List.of(101L, 102L);

        User user = new User();
        user.setUserId(userId);

        Show show = new Show();
        show.setShowId(showId);

        Seat seat1 = new Seat(1, SeatType.REGULAR, SeatStatus.AVAILABLE, show);
        Seat seat2 = new Seat(2, SeatType.VIP, SeatStatus.AVAILABLE, show);

        BookingRequestDTO request = new BookingRequestDTO(userId, showId, seatIds);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(showRepository.findById(showId)).thenReturn(Optional.of(show));
        when(seatRepository.findAllByIdWithLock(seatIds)).thenReturn(List.of(seat1, seat2));
        when(bookingRepository.save(any())).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setBookingId(999L);
            return booking;
        });

        String confirmation = bookingService.bookMovie(request);

        assertTrue(confirmation.contains("Booking confirmed. ID: 999"));
        assertTrue(confirmation.contains("1, 2"));
        assertTrue(confirmation.contains("Total Amount: ₹600"));
        verify(seatRepository).saveAll(any());
    }

    @Test
    void testBookMovie_SeatAlreadyBooked() {
        Long userId = 1L;
        Long showId = 10L;
        List<Long> seatIds = List.of(101L);

        User user = new User();
        Show show = new Show();
        Seat seat = new Seat(1, SeatType.REGULAR, SeatStatus.BOOKED, show);

        BookingRequestDTO request = new BookingRequestDTO(userId, showId, seatIds);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(showRepository.findById(showId)).thenReturn(Optional.of(show));
        when(seatRepository.findAllByIdWithLock(seatIds)).thenReturn(List.of(seat));

        assertThrows(SeatNotAvailableException.class, () -> bookingService.bookMovie(request));
    }

    @Test
    void testBookMovie_UserNotFound() {
        BookingRequestDTO request = new BookingRequestDTO(1L, 2L, List.of(1L));

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> bookingService.bookMovie(request));
    }

    @Test
    void testBookMovie_ShowNotFound() {
        User user = new User();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(showRepository.findById(2L)).thenReturn(Optional.empty());

        BookingRequestDTO request = new BookingRequestDTO(1L, 2L, List.of(1L));

        assertThrows(ShowNotFoundException.class, () -> bookingService.bookMovie(request));
    }

    @Test
    void testGetUserBookingHistory() {
        Long userId = 1L;
        User user = new User();
        user.setUserId(userId);

        Movie movie = new Movie();
        movie.setTitle("Inception");

        Show show = new Show();
        show.setMovie(movie);
        show.setShowTime(LocalDateTime.now());

        Seat seat = new Seat(1, SeatType.REGULAR, SeatStatus.BOOKED, show);

        Booking booking = new Booking();
        booking.setBookingId(100L);
        booking.setShow(show);
        booking.setBookedSeats(List.of(seat));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByUser_UserId(userId)).thenReturn(List.of(booking));

        List<BookingResponseDTO> response = bookingService.getUserBookingHistory(userId);

        assertEquals(1, response.size());
        assertEquals("Inception", response.get(0).getMovieTitle());
        assertEquals(List.of(1), response.get(0).getBookedSeats());
    }
}
