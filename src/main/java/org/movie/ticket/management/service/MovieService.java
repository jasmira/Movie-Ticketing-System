package org.movie.ticket.management.service;

import org.movie.ticket.management.entity.Movie;
import org.movie.ticket.management.repository.ShowRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {
    public final ShowRepository showRepository;

    public MovieService(ShowRepository showRepository) {
        this.showRepository = showRepository;
    }

    public List<Movie> getMoviesByCity(String city) {
        return showRepository.findDistinctMoviesByCity(city);
    }
}
