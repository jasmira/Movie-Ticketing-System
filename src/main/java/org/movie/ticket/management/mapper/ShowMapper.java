package org.movie.ticket.management.mapper;

import org.movie.ticket.management.dto.ShowDTO;
import org.movie.ticket.management.entity.Show;
import org.springframework.stereotype.Component;

@Component
public class ShowMapper {
    public ShowDTO convertToDTO(Show show) {
        return new ShowDTO(
                show.getShowId(),
                show.getMovie().getTitle(),
                show.getMovie().getGenre(),
                show.getMovie().getLanguage(),
                show.getMovie().getDuration(),
                show.getTheater().getName(),
                show.getTheater().getCity()
        );
    }
}
