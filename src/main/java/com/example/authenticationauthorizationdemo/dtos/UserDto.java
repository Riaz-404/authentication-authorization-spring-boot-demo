package com.example.authenticationauthorizationdemo.dtos;

import lombok.Value;

@Value
public class UserDto {
    String email;
    String password;
}
