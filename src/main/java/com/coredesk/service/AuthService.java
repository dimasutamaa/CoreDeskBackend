package com.coredesk.service;

import com.coredesk.exception.AppException;
import com.coredesk.dto.AuthRequest;
import com.coredesk.dto.AuthResponse;
import com.coredesk.mapper.UserMapper;
import com.coredesk.model.User;
import com.coredesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final SessionStore sessionStore;

    public User createUser(User user) throws AppException {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new AppException("User already exist", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public AuthResponse login(AuthRequest request) throws AppException {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException e) {
            throw new AppException("Invalid email or password", HttpStatus.BAD_REQUEST);
        }

        User user = (User) authentication.getPrincipal();

        String token = jwtTokenService.generateAccessToken(user);
        sessionStore.put(user.getEmail(), token); // overwrites existing session

        return new AuthResponse(token, 86400L, userMapper.toDto(user));
    }

    public void logout(String email) {
        sessionStore.remove(email);
        SecurityContextHolder.clearContext();
    }

}
