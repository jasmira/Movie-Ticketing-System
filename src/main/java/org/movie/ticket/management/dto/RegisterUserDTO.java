package org.movie.ticket.management.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.movie.ticket.management.entity.Role;

@AllArgsConstructor
@Getter
@Setter
public class RegisterUserDTO {
    private String name;
    private String email;
    private String password;
    private Role role;
}
