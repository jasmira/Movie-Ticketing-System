package org.movie.ticket.management.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Entity
@NoArgsConstructor
@Data
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieId;
    private String title;
    private String genre;
    private String language;
    private Duration duration;

    public Movie(String title, String genre, String language, Duration duration) {
        this.title = title;
        this.genre = genre;
        this.language = language;
        this.duration = duration;
    }
}
