package org.movie.ticket.management.config;

import jakarta.annotation.PostConstruct;
import org.movie.ticket.management.entity.*;
import org.movie.ticket.management.repository.MovieRepository;
import org.movie.ticket.management.repository.SeatRepository;
import org.movie.ticket.management.repository.ShowRepository;
import org.movie.ticket.management.repository.TheaterRepository;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DBInitializer {
    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;

    public DBInitializer(MovieRepository movieRepository,
                         TheaterRepository theaterRepository,
                         ShowRepository showRepository,
                         SeatRepository seatRepository) {
        this.movieRepository = movieRepository;
        this.theaterRepository = theaterRepository;
        this.showRepository = showRepository;
        this.seatRepository = seatRepository;
    }

    @PostConstruct
    public void init() {
        // Movies
        Movie movie1 = new Movie("Inception", "Sci-Fi", "English", Duration.ofMinutes(148));
        Movie movie2 = new Movie("3 Idiots", "Comedy", "Hindi", Duration.ofMinutes(170));
        movieRepository.saveAll(List.of(movie1, movie2));

        // Theaters
        Theater theater1 = new Theater();
        theater1.setName("INOX Panjim");
        theater1.setCity("Goa");
        theaterRepository.save(theater1);
        Theater theater2 = new Theater();
        theater2.setName("OSIA");
        theater2.setCity("Mumbai");
        theaterRepository.save(theater2);

        // Shows
        Show show1 = new Show();
        show1.setMovie(movie1);
        show1.setTheater(theater1);
        show1.setShowTime(LocalDateTime.now().plusDays(1).withHour(18).withMinute(30));
        show1 = showRepository.save(show1);
        Show show2 = new Show();
        show2.setMovie(movie1);
        show2.setTheater(theater2);
        show2.setShowTime(LocalDateTime.now().plusDays(1).withHour(14).withMinute(15));
        show2 = showRepository.save(show2);
        Show show3 = new Show();
        show3.setMovie(movie2);
        show3.setTheater(theater1);
        show3.setShowTime(LocalDateTime.now().plusDays(1).withHour(16).withMinute(23));
        show3 = showRepository.save(show3);

        // Seats
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Seat seat = new Seat();
            seat.setSeatNumber(i);
            seat.setSeatType(i <= 5 ? SeatType.REGULAR : SeatType.VIP);
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setShow(show1);
            seats.add(seat);
        }
        seatRepository.saveAll(seats);

        List<Seat> seats2 = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Seat seat = new Seat();
            seat.setSeatNumber(i);
            seat.setSeatType(i <= 5 ? SeatType.REGULAR : SeatType.VIP);
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setShow(show2);
            seats2.add(seat);
        }
        seatRepository.saveAll(seats2);

        List<Seat> seats3 = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            Seat seat = new Seat();
            seat.setSeatNumber(i);
            seat.setSeatType(i <= 10 ? SeatType.REGULAR : SeatType.VIP);
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setShow(show3);
            seats3.add(seat);
        }
        seatRepository.saveAll(seats3);

        // Link seats to show (if bi-directional mapping is used)
        show1.setSeats(seats);
        showRepository.save(show1);

        show2.setSeats(seats2);
        showRepository.save(show1);

        show1.setSeats(seats3);
        showRepository.save(show3);
    }
}
