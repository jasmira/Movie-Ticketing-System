package org.movie.ticket.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.movie.ticket.management.config.GlobalExceptionHandler;
import org.movie.ticket.management.dto.LoginUserDTO;
import org.movie.ticket.management.exception.InvalidCredentialsException;
import org.movie.ticket.management.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginService loginService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(AbstractHttpConfigurer::disable) // disable CSRF protection
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/api/auth/login", "/api/users", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                            .anyRequest().authenticated()
                    );
            return http.build();
        }
    }

    @Test
    void testLoginSuccess_ReturnsJwtToken() throws Exception {
        // Arrange
        LoginUserDTO loginUserDTO = new LoginUserDTO("john@example.com", "password123");

        String mockToken = "mock-jwt-token";

        when(loginService.loginUser(loginUserDTO)).thenReturn(mockToken);

        // Act + Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void testLoginInvalidUser_ThrowsError() throws Exception {
        // Arrange
        LoginUserDTO loginUserDTO = new LoginUserDTO("invalid", "wrong");

        when(loginService.loginUser(any(LoginUserDTO.class)))
                .thenThrow(new InvalidCredentialsException("Invalid credentials"));

        // Act + Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserDTO)))
                .andExpect(status().isUnauthorized())  // Now this matches
                .andExpect(content().string("Invalid credentials"));
    }
}
