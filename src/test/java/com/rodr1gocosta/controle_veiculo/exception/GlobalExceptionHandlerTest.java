package com.rodr1gocosta.controle_veiculo.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("deve retornar ProblemDetail 404 quando VeiculoNotFoundException é lançada")
    void handleVeiculoNotFound_deveRetornar404() {
        // Arrange
        UUID veiculoId = UUID.randomUUID();
        VeiculoNotFoundException exception = new VeiculoNotFoundException(veiculoId);

        // Act
        ProblemDetail result = globalExceptionHandler.handleVeiculoNotFound(exception);

        // Assert
        assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(result.getTitle()).isEqualTo("Veículo não encontrado");
        assertThat(result.getDetail()).isEqualTo("Veículo não encontrado com id: " + veiculoId);
        assertThat(result.getType()).isEqualTo(URI.create("/errors/not-found"));
    }

    @Test
    @DisplayName("deve retornar ProblemDetail 422 quando MethodArgumentNotValidException é lançada")
    void handleValidation_deveRetornar422ComErrosDeValidacao() {
        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("veiculoRequest", "marca", "não deve estar vazio");
        FieldError fieldError2 = new FieldError("veiculoRequest", "ano", "deve ser maior que 1900");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // Act
        ProblemDetail result = globalExceptionHandler.handleValidation(exception);

        // Assert
        assertThat(result.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(result.getTitle()).isEqualTo("Erro de validação");
        assertThat(result.getDetail()).isEqualTo("Dados inválidos");
        assertThat(result.getType()).isEqualTo(URI.create("/errors/validation"));

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) result.getProperties().get("errors");
        assertThat(errors).containsEntry("marca", "não deve estar vazio");
        assertThat(errors).containsEntry("ano", "deve ser maior que 1900");
    }

    @Test
    @DisplayName("deve retornar ProblemDetail 500 quando Exception genérica é lançada")
    void handleGeneral_deveRetornar500() {
        // Arrange
        Exception exception = new RuntimeException("Erro inesperado no sistema");

        // Act
        ProblemDetail result = globalExceptionHandler.handleGeneral(exception);

        // Assert
        assertThat(result.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(result.getTitle()).isEqualTo("Erro interno");
        assertThat(result.getDetail()).isEqualTo("Erro inesperado no sistema");
        assertThat(result.getType()).isEqualTo(URI.create("/errors/internal"));
    }

    @Test
    @DisplayName("deve retornar ProblemDetail 503 quando CotacaoIndisponivelException é lançada")
    void handleCotacaoIndisponivel_deveRetornar503() {
        // Arrange
        CotacaoIndisponivelException exception = new CotacaoIndisponivelException();

        // Act
        ProblemDetail result = globalExceptionHandler.handleCotacaoIndisponivel(exception);

        // Assert
        assertThat(result.getStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value());
        assertThat(result.getTitle()).isEqualTo("Serviço Indisponível");
        assertThat(result.getDetail()).isEqualTo("Serviço de cotação do dólar indisponível. Tente novamente mais tarde.");
        assertThat(result.getType()).isEqualTo(URI.create("/errors/cotacao-indisponivel"));
    }
}