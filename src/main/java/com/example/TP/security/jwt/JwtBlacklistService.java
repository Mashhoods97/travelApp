package com.example.TP.security.jwt;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class JwtBlacklistService {

    private Set<String> tokenBlacklist = new HashSet<>();

    public void addToBlacklist(String token) {
        tokenBlacklist.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }
}
