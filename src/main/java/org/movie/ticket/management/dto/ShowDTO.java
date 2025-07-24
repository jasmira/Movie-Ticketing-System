package org.movie.ticket.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ShowDTO {
    private Long showId;
    private String movieTitle;
    private String genre;
    private String language;
    private Duration duration;
    private String theaterName;
    private String theaterCity;
}
