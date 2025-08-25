package com.example.TP.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private Long stationId;
}
