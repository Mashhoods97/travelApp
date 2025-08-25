package com.example.TP.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class JwtResponse {
    private final String accessToken;
    private final String tokenType = "Bearer";
    private final Long id;
    private final String username;
    private final String email;
    private final Integer type;
    private final List<String> privileges;
}
