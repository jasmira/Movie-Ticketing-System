package org.movie.ticket.management.service;

import org.movie.ticket.management.dto.LoginUserDTO;
import org.movie.ticket.management.entity.User;
import org.movie.ticket.management.exception.InvalidCredentialsException;
import org.movie.ticket.management.exception.UserNotFoundException;
import org.movie.ticket.management.repository.UserRepository;
import org.movie.ticket.management.security.JwtTokenUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Service
public class LoginService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authManager;

    public LoginService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil, AuthenticationManager authManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authManager = authManager;
    }

    public String loginUser(LoginUserDTO loginUserDTO) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserDTO.getEmail(), loginUserDTO.getPassword())
        );

        User user = userRepository.findByEmail(loginUserDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User with Email: " +loginUserDTO.getEmail()+ " not found."));

        if (!passwordEncoder.matches(loginUserDTO.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials.");
        }

        return jwtTokenUtil.generateToken(user);
    }
}
