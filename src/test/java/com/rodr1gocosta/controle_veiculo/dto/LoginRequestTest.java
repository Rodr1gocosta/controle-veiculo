package com.rodr1gocosta.controle_veiculo.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest {

    @Test
    @DisplayName("username() deve retornar o username correto")
    void username() {
        // Arrange
        LoginRequest request = new LoginRequest("admin", "admin123");

        // Act & Assert
        assertThat(request.username()).isEqualTo("admin");
    }

    @Test
    @DisplayName("password() deve retornar a senha correta")
    void password() {
        // Arrange
        LoginRequest request = new LoginRequest("admin", "admin123");

        // Act & Assert
        assertThat(request.password()).isEqualTo("admin123");
    }

    @Test
    @DisplayName("dois registros com mesmos valores devem ser iguais")
    void igualdade_deveSerPorValor() {
        // Arrange
        LoginRequest r1 = new LoginRequest("user", "user123");
        LoginRequest r2 = new LoginRequest("user", "user123");

        // Act & Assert
        assertThat(r1).isEqualTo(r2);
    }

    @Test
    @DisplayName("registros com passwords diferentes devem ser diferentes")
    void igualdade_comPasswordsDiferentes_deveSerDiferente() {
        // Arrange
        LoginRequest r1 = new LoginRequest("admin", "senha1");
        LoginRequest r2 = new LoginRequest("admin", "senha2");

        // Act & Assert
        assertThat(r1).isNotEqualTo(r2);
    }

    @Test
    @DisplayName("deve aceitar username e password nulos")
    void camposNulos_devemSerPermitidos() {
        // Arrange
        LoginRequest request = new LoginRequest(null, null);

        // Act & Assert
        assertThat(request.username()).isNull();
        assertThat(request.password()).isNull();
    }
}