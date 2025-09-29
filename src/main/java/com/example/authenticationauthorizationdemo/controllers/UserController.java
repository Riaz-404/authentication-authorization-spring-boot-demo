package com.example.authenticationauthorizationdemo.controllers;

import com.example.authenticationauthorizationdemo.dtos.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class UserController {
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String getUser () {
        return "Hello from get request";
    }

    @PostMapping
    public void createNewUser (@Validated @RequestBody UserDto user) {

    }
}
