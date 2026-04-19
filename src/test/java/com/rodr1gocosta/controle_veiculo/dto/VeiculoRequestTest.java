package com.rodr1gocosta.controle_veiculo.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VeiculoRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("marca() deve retornar a marca correta")
    void marca() {
        // Arrange
        VeiculoRequest request = new VeiculoRequest("Toyota", 2022, "Prata", new BigDecimal("100000.00"), "ABC-1234");

        // Act & Assert
        assertThat(request.marca()).isEqualTo("Toyota");
    }

    @Test
    @DisplayName("marca() em branco deve gerar violação de validação")
    void marca_emBranco_deveGerarViolacao() {
        // Arrange
        VeiculoRequest request = new VeiculoRequest("", 2022, "Prata", new BigDecimal("100000.00"), "ABC-1234");

        // Act
        Set<ConstraintViolation<VeiculoRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("marca"));
    }

    @Test
    @DisplayName("ano() deve retornar o ano correto")
    void ano() {
        // Arrange
        VeiculoRequest request = new VeiculoRequest("Toyota", 2022, "Prata", new BigDecimal("100000.00"), "ABC-1234");

        // Act & Assert
        assertThat(request.ano()).isEqualTo(2022);
    }

    @Test
    @DisplayName("ano() abaixo do mínimo deve gerar violação de validação")
    void ano_abaixoDoMinimo_deveGerarViolacao() {
        // Arrange
        VeiculoRequest request = new VeiculoRequest("Toyota", 1800, "Prata", new BigDecimal("100000.00"), "ABC-1234");

        // Act
        Set<ConstraintViolation<VeiculoRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("ano"));
    }

    @Test
    @DisplayName("cor() deve retornar a cor correta")
    void cor() {
        // Arrange
        VeiculoRequest request = new VeiculoRequest("Toyota", 2022, "Prata", new BigDecimal("100000.00"), "ABC-1234");

        // Act & Assert
        assertThat(request.cor()).isEqualTo("Prata");
    }

    @Test
    @DisplayName("cor() em branco deve gerar violação de validação")
    void cor_emBranco_deveGerarViolacao() {
        // Arrange
        VeiculoRequest request = new VeiculoRequest("Toyota", 2022, "", new BigDecimal("100000.00"), "ABC-1234");

        // Act
        Set<ConstraintViolation<VeiculoRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("cor"));
    }

    @Test
    @DisplayName("preco() deve retornar o preço correto")
    void preco() {
        // Arrange
        VeiculoRequest request = new VeiculoRequest("Toyota", 2022, "Prata", new BigDecimal("100000.00"), "ABC-1234");

        // Act & Assert
        assertThat(request.preco()).isEqualByComparingTo(new BigDecimal("100000.00"));
    }

    @Test
    @DisplayName("preco() zero deve gerar violação de validação")
    void preco_zero_deveGerarViolacao() {
        // Arrange
        VeiculoRequest request = new VeiculoRequest("Toyota", 2022, "Prata", BigDecimal.ZERO, "ABC-1234");

        // Act
        Set<ConstraintViolation<VeiculoRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("preco"));
    }

    @Test
    @DisplayName("placa() deve retornar a placa correta")
    void placa() {
        // Arrange
        VeiculoRequest request = new VeiculoRequest("Toyota", 2022, "Prata", new BigDecimal("100000.00"), "ABC-1234");

        // Act & Assert
        assertThat(request.placa()).isEqualTo("ABC-1234");
    }

    @Test
    @DisplayName("placa() em branco deve gerar violação de validação")
    void placa_emBranco_deveGerarViolacao() {
        // Arrange
        VeiculoRequest request = new VeiculoRequest("Toyota", 2022, "Prata", new BigDecimal("100000.00"), "");

        // Act
        Set<ConstraintViolation<VeiculoRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("placa"));
    }

    @Test
    @DisplayName("builder deve criar VeiculoRequest com todos os campos válidos")
    void builder() {
        // Act
        VeiculoRequest request = VeiculoRequest.builder()
                .marca("Honda")
                .ano(2023)
                .cor("Preto")
                .preco(new BigDecimal("80000.00"))
                .placa("XYZ-9999")
                .build();

        // Assert
        assertThat(request.marca()).isEqualTo("Honda");
        assertThat(request.ano()).isEqualTo(2023);
        assertThat(request.cor()).isEqualTo("Preto");
        assertThat(request.preco()).isEqualByComparingTo(new BigDecimal("80000.00"));
        assertThat(request.placa()).isEqualTo("XYZ-9999");

        Set<ConstraintViolation<VeiculoRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }
}