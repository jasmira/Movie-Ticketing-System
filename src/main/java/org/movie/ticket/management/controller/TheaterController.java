package org.movie.ticket.management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.movie.ticket.management.dto.ShowDTO;
import org.movie.ticket.management.entity.Show;
import org.movie.ticket.management.mapper.ShowMapper;
import org.movie.ticket.management.service.TheaterService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/theaters")
@Tag(name = "Theater Details", description = "Manage Theater.")
public class TheaterController {
    public final TheaterService theaterService;
    public final ShowMapper showMapper;

    public TheaterController(TheaterService theaterService, ShowMapper showMapper) {
        this.theaterService = theaterService;
        this.showMapper = showMapper;
    }

    @Operation(summary = "Get all shows running in a theater")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}/shows")
    public ResponseEntity<List<ShowDTO>> getShowListForTheater(@PathVariable Long id) {
        List<Show> shows = theaterService.getShowListForTheater(id);
        List<ShowDTO> showDTOs = shows.stream()
                .map(showMapper::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(showDTOs);
    }
}
