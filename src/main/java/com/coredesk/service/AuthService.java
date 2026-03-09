package com.coredesk.service;

import com.coredesk.exception.AppException;
import com.coredesk.dto.AuthRequest;
import com.coredesk.dto.AuthResponse;
import com.coredesk.dto.UserInfo;
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

    public User createUser(User user) throws AppException {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new AppException("User already exist", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public AuthResponse login(AuthRequest request) throws AppException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e) {
            throw new AppException("Invalid email or password", HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        String token = jwtTokenService.generateAccessToken(user);

        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getDisplayName());
        userInfo.setEmail(user.getEmail());
        userInfo.setRole(user.getRole());

        return new AuthResponse(token, 86400L, userInfo);
    }

}
