package org.movie.ticket.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.movie.ticket.management.config.GlobalExceptionHandler;
import org.movie.ticket.management.entity.Movie;
import org.movie.ticket.management.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
@Import({GlobalExceptionHandler.class})
@WithMockUser(roles = "USER")
public class MovieControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/api/auth/login", "/api/users", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                            .anyRequest().authenticated()
                    );
            return http.build();
        }
    }

    @Test
    void testGetMoviesForCity_ReturnsMovieList() throws Exception {
        // Arrange
        String city = "Goa";
        Movie movie = new Movie();
        movie.setMovieId(1L);
        movie.setTitle("Inception");
        movie.setGenre("Sci-Fi");
        movie.setLanguage("English");

        when(movieService.getMoviesByCity(city)).thenReturn(List.of(movie));

        // Act + Assert
        mockMvc.perform(get("/api/movies")
                        .param("city", city)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Inception"))
                .andExpect(jsonPath("$[0].movieId").value(1));
    }

    @Test
    void testGetMoviesForCity_EmptyList_Returns200() throws Exception {
        // Arrange
        String city = "Unknown";
        when(movieService.getMoviesByCity(city)).thenReturn(List.of());

        // Act + Assert
        mockMvc.perform(get("/api/movies")
                        .param("city", city)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
