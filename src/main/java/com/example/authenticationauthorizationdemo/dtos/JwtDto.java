package com.example.authenticationauthorizationdemo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class JwtDto {
    private String token;
}
