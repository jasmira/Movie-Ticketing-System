package org.movie.ticket.management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.movie.ticket.management.entity.Movie;
import org.movie.ticket.management.service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@Tag(name = "Movie Details", description = "Manage Movies.")
public class MovieController {
    public final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @Operation(summary = "Get all movies running in a city")
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<List<Movie>> getMoviesForCity(@RequestParam String city) {
        List<Movie> movies = movieService.getMoviesByCity(city);
        return ResponseEntity.ok(movies);
    }
}
