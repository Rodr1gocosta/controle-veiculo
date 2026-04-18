package com.rodr1gocosta.controle_veiculo.dto;

public record LoginResponse(String token, String type, long expiresIn) {
    public LoginResponse(String token, long expiresIn) {
        this(token, "Bearer", expiresIn);
    }
}

