package org.movie.ticket.management.service;

import org.movie.ticket.management.entity.Show;
import org.movie.ticket.management.exception.ShowNotFoundException;
import org.movie.ticket.management.repository.ShowRepository;
import org.springframework.stereotype.Service;

@Service
public class ShowService {
    public final ShowRepository showRepository;

    public ShowService(ShowRepository showRepository) {
        this.showRepository = showRepository;
    }

    public Show getShow(Long showId) {
        return showRepository.findById(showId).orElseThrow(() -> new ShowNotFoundException("Show with ID: " +showId+ " not found"));
    }
}
