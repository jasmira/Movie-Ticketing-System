package org.movie.ticket.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.movie.ticket.management.config.GlobalExceptionHandler;
import org.movie.ticket.management.dto.RegisterUserDTO;
import org.movie.ticket.management.entity.Role;
import org.movie.ticket.management.service.UserService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

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
    void testRegisterUser_Success_ReturnsOk() throws Exception {
        // Arrange
        RegisterUserDTO registerUserDTO = new RegisterUserDTO("John", "john@example.com", "password123", Role.USER);
        String mockResponse = "User registered successfully.";

        when(userService.registerNewUser(any(RegisterUserDTO.class))).thenReturn(mockResponse);

        // Act + Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUserDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(mockResponse));
    }

    @Test
    void testRegisterUser_Failure_ThrowsException() throws Exception {
        // Arrange
        RegisterUserDTO registerUserDTO = new RegisterUserDTO("Jane", "jane@example.com", "weak", Role.USER);

        when(userService.registerNewUser(any(RegisterUserDTO.class)))
                .thenThrow(new RuntimeException("Registration failed"));

        // Act + Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUserDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Registration failed"));
    }
}
