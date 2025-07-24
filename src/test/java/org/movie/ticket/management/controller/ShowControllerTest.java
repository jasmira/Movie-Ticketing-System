package org.movie.ticket.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.movie.ticket.management.config.GlobalExceptionHandler;
import org.movie.ticket.management.dto.ShowDTO;
import org.movie.ticket.management.entity.Show;
import org.movie.ticket.management.exception.ShowNotFoundException;
import org.movie.ticket.management.mapper.ShowMapper;
import org.movie.ticket.management.service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShowController.class)
@Import({GlobalExceptionHandler.class})
@WithMockUser(roles = "USER")
public class ShowControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShowService showService;

    @MockBean
    private ShowMapper showMapper;

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
    void testGetShowDetail_ReturnsShowDTO() throws Exception {
        // Arrange
        Long showId = 1L;

        Show mockShow = new Show();
        mockShow.setShowId(showId);

        ShowDTO mockDTO = new ShowDTO();
        mockDTO.setShowId(showId);
        mockDTO.setDuration(Duration.ofHours(2).plusMinutes(28));

        when(showService.getShow(showId)).thenReturn(mockShow);
        when(showMapper.convertToDTO(mockShow)).thenReturn(mockDTO);

        // Act + Assert
        mockMvc.perform(get("/api/shows/{id}", showId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.showId").value(showId));
    }

    @Test
    void testGetShowDetail_ShowNotFound_Returns404() throws Exception {
        // Arrange
        Long invalidId = 99L;
        when(showService.getShow(invalidId))
                .thenThrow(new ShowNotFoundException("Show with ID: 99 not found"));

        // Act + Assert
        mockMvc.perform(get("/api/shows/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Show with ID: 99 not found"));
    }
}
