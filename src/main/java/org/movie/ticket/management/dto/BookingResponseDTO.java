package org.movie.ticket.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class BookingResponseDTO {
    private Long bookingId;
    private String movieTitle;
    private LocalDateTime showTime;
    private List<Integer> bookedSeats;
}
