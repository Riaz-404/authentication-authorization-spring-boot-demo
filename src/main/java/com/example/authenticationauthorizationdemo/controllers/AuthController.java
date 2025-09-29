package com.example.authenticationauthorizationdemo.controllers;

import com.example.authenticationauthorizationdemo.config.JwtUtils;
import com.example.authenticationauthorizationdemo.dtos.JwtDto;
import com.example.authenticationauthorizationdemo.dtos.UserDto;
import com.example.authenticationauthorizationdemo.models.User;
import com.example.authenticationauthorizationdemo.repositories.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Validated @RequestBody UserDto user) {
        var encodedPassword = passwordEncoder.encode(user.getPassword());
        User newUser = new User(user.getEmail(), encodedPassword);
        User createdUser = userRepository.save(newUser);

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtDto> login(@Validated @RequestBody UserDto user, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        user.getPassword()
                )
        );

        String accessToken = jwtUtils.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        var cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(3600 * 24 * 15);
        cookie.setSecure(true);

        response.addCookie(cookie);

        return new ResponseEntity<>(new JwtDto(accessToken), HttpStatus.OK);
    }

    @PostMapping("/validate")
    public boolean validateToken(@RequestHeader("Authorization") String token) {
        String parsedToken = token.replace("Bearer ", "");

        return jwtUtils.validateToken(parsedToken);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> refreshAccessToken(@CookieValue("refreshToken") String refreshToken) {
        if (!jwtUtils.validateToken(refreshToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var userEmail = jwtUtils.getUserEmailFromToken(refreshToken);
        var newAccessToken = jwtUtils.generateAccessToken(userEmail);

        return ResponseEntity.status(HttpStatus.OK).body(new JwtDto(newAccessToken));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialException () {
        return ResponseEntity.status(401).build();
    }
}
