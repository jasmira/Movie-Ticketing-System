package org.movie.ticket.management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.movie.ticket.management.dto.RegisterUserDTO;
import org.movie.ticket.management.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Details", description = "Manage Users.")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Register a new user")
    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody RegisterUserDTO registerUserDTO) {
        String response = userService.registerNewUser(registerUserDTO);
        return ResponseEntity.ok(response);
    }
}
