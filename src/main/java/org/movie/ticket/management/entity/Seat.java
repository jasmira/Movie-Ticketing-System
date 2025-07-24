package org.movie.ticket.management.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;

    private int seatNumber;

    @Enumerated(EnumType.STRING)
    private SeatType seatType; // (REGULAR, VIP);

    @Enumerated(EnumType.STRING)
    private SeatStatus status; // (AVAILABLE, BOOKED);

    @ManyToOne
    @JoinColumn(name = "show_id")
    private Show show;

    public Seat(int seatNumber, SeatType seatType, SeatStatus status, Show show) {
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.status = status;
        this.show = show;
    }
}
