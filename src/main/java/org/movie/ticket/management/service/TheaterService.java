package org.movie.ticket.management.service;

import org.movie.ticket.management.entity.Show;
import org.movie.ticket.management.entity.Theater;
import org.movie.ticket.management.exception.TheaterNotFoundException;
import org.movie.ticket.management.repository.TheaterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TheaterService {
    public final TheaterRepository theaterRepository;

    public TheaterService(TheaterRepository theaterRepository) {
        this.theaterRepository = theaterRepository;
    }
    public List<Show> getShowListForTheater(Long theaterId) {
        Theater theater = theaterRepository.findById(theaterId).orElseThrow(() -> new TheaterNotFoundException("Theater with ID: "+theaterId+ " not found"));
        return theater.getShows();
    }
}
