package org.movie.ticket.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.movie.ticket.management.config.GlobalExceptionHandler;
import org.movie.ticket.management.dto.BookingRequestDTO;
import org.movie.ticket.management.dto.BookingResponseDTO;
import org.movie.ticket.management.security.CustomUserDetailsService;
import org.movie.ticket.management.security.JwtTokenUtil;
import org.movie.ticket.management.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@Import({GlobalExceptionHandler.class, BookingControllerTest.TestSecurityConfig.class})
public class BookingControllerTest {
    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/api/bookings/**").hasRole("ADMIN")
                            .anyRequest().authenticated()
                    );
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testBookMovie_ReturnsConfirmationMessage() throws Exception {
        // Arrange
        BookingRequestDTO requestDTO = new BookingRequestDTO();
        requestDTO.setUserId(1L);
        requestDTO.setShowId(10L);
        requestDTO.setSeatIds(List.of(100L, 101L));

        String mockResponse = "Booking confirmed. ID: 55 | Seats: A1, A2 | Total Amount: ₹400";

        when(bookingService.bookMovie(any(BookingRequestDTO.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(mockResponse));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserBookings_ReturnsBookingHistory() throws Exception {
        // Arrange
        Long userId = 1L;

        BookingResponseDTO mockBooking = new BookingResponseDTO(
                55L,
                "Inception",
                LocalDateTime.of(2025, 7, 24, 18, 30),
                List.of(1, 2)
        );

        when(bookingService.getUserBookingHistory(userId)).thenReturn(List.of(mockBooking));

        // Act & Assert
        mockMvc.perform(get("/api/bookings/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingId").value(55))
                .andExpect(jsonPath("$[0].movieTitle").value("Inception"))
                .andExpect(jsonPath("$[0].bookedSeats.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "USER") // This simulates a logged-in USER, not ADMIN
    void testBookMovie_ForbiddenForNonAdmin() throws Exception {
        BookingRequestDTO requestDTO = new BookingRequestDTO();
        requestDTO.setUserId(1L);
        requestDTO.setShowId(10L);
        requestDTO.setSeatIds(List.of(100L));

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());

        verify(bookingService, never()).bookMovie(any());
    }
}
