package org.movie.ticket.management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.movie.ticket.management.dto.ShowDTO;
import org.movie.ticket.management.entity.Show;
import org.movie.ticket.management.mapper.ShowMapper;
import org.movie.ticket.management.service.ShowService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shows")
@Tag(name = "Show Details", description = "Manage Shows.")
public class ShowController {
    public final ShowService showService;
    public final ShowMapper showMapper;

    public ShowController(ShowService showService, ShowMapper showMapper) {
        this.showService = showService;
        this.showMapper = showMapper;
    }

    @Operation(summary = "Get show details by Id")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<ShowDTO> getShowDetail(@PathVariable Long id) {
        Show show = showService.getShow(id);
        ShowDTO showDTO = showMapper.convertToDTO(show);
        return ResponseEntity.ok(showDTO);
    }
}
