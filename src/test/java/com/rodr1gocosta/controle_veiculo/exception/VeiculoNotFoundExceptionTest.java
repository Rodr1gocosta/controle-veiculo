package com.rodr1gocosta.controle_veiculo.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VeiculoNotFoundExceptionTest {

    @Test
    @DisplayName("deve criar exceção com mensagem contendo o ID do veículo")
    void deveCriarExcecaoComMensagemContendoId() {
        // Arrange
        UUID veiculoId = UUID.randomUUID();

        // Act
        VeiculoNotFoundException exception = new VeiculoNotFoundException(veiculoId);

        // Assert
        assertThat(exception.getMessage())
                .isEqualTo("Veículo não encontrado com id: " + veiculoId);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("deve ser uma RuntimeException")
    void deveSerRuntimeException() {
        // Arrange
        UUID veiculoId = UUID.randomUUID();

        // Act
        VeiculoNotFoundException exception = new VeiculoNotFoundException(veiculoId);

        // Assert
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("deve incluir UUID no formato correto na mensagem")
    void deveIncluirUuidNoFormatoCorretoNaMensagem() {
        // Arrange
        UUID veiculoId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        // Act
        VeiculoNotFoundException exception = new VeiculoNotFoundException(veiculoId);

        // Assert
        assertThat(exception.getMessage())
                .contains("123e4567-e89b-12d3-a456-426614174000")
                .startsWith("Veículo não encontrado com id:");
    }
}