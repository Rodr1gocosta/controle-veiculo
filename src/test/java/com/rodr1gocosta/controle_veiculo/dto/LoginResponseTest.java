package com.rodr1gocosta.controle_veiculo.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginResponseTest {

    @Test
    @DisplayName("token() deve retornar o token correto")
    void token() {
        // Arrange
        LoginResponse response = new LoginResponse("meu.jwt.token", 28800L);

        // Act & Assert
        assertThat(response.token()).isEqualTo("meu.jwt.token");
    }

    @Test
    @DisplayName("type() deve retornar 'Bearer' por padrão quando criado pelo construtor de conveniência")
    void type() {
        // Arrange
        LoginResponse response = new LoginResponse("meu.jwt.token", 28800L);

        // Act & Assert
        assertThat(response.type()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("type() deve retornar o tipo informado quando criado pelo construtor completo")
    void type_construtorCompleto() {
        // Arrange
        LoginResponse response = new LoginResponse("meu.jwt.token", "Custom", 28800L);

        // Act & Assert
        assertThat(response.type()).isEqualTo("Custom");
    }

    @Test
    @DisplayName("expiresIn() deve retornar o tempo de expiração correto")
    void expiresIn() {
        // Arrange
        long expiracao = 28800L; // 8 horas em segundos

        // Act
        LoginResponse response = new LoginResponse("meu.jwt.token", expiracao);

        // Assert
        assertThat(response.expiresIn()).isEqualTo(28800L);
    }

    @Test
    @DisplayName("construtor de conveniência deve definir type como 'Bearer' automaticamente")
    void construtorConveniencia_deveDefinirTypeBearer() {
        // Arrange & Act
        LoginResponse response = new LoginResponse("eyJhbGciOiJSUzI1NiJ9...", 3600L);

        // Assert
        assertThat(response.token()).isEqualTo("eyJhbGciOiJSUzI1NiJ9...");
        assertThat(response.type()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(3600L);
    }

    @Test
    @DisplayName("dois registros com mesmos valores devem ser iguais")
    void igualdade_deveSerPorValor() {
        // Arrange
        LoginResponse r1 = new LoginResponse("token", "Bearer", 28800L);
        LoginResponse r2 = new LoginResponse("token", "Bearer", 28800L);

        // Act & Assert
        assertThat(r1).isEqualTo(r2);
    }
}