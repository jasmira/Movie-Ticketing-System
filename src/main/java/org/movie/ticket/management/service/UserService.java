package org.movie.ticket.management.service;

import org.movie.ticket.management.dto.RegisterUserDTO;
import org.movie.ticket.management.entity.User;
import org.movie.ticket.management.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String registerNewUser(RegisterUserDTO registerUserDTO) {
        String hashedPassword = passwordEncoder.encode(registerUserDTO.getPassword());
        User user = new User(registerUserDTO.getName(), registerUserDTO.getEmail(), hashedPassword, registerUserDTO.getRole());
        User registeredUser = userRepository.save(user);
        return "New User with ID: " +registeredUser.getUserId()+ " registered successfully.";
    }
}
