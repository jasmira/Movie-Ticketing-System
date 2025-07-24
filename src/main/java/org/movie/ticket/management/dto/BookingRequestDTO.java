package org.movie.ticket.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class BookingRequestDTO {
    private Long userId;
    private Long showId;
    private List<Long> seatIds;
}
