package com.rodr1gocosta.controle_veiculo.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class VeiculoPatchRequestTest {

    @Test
    @DisplayName("marca() deve retornar a marca correta")
    void marca() {
        // Arrange
        VeiculoPatchRequest request = new VeiculoPatchRequest("Honda", null, null, null, null);

        // Act & Assert
        assertThat(request.marca()).isEqualTo("Honda");
    }

    @Test
    @DisplayName("marca() deve aceitar null para atualização parcial")
    void marca_null_deveSerPermitido() {
        // Arrange
        VeiculoPatchRequest request = new VeiculoPatchRequest(null, null, null, null, null);

        // Act & Assert
        assertThat(request.marca()).isNull();
    }

    @Test
    @DisplayName("ano() deve retornar o ano correto")
    void ano() {
        // Arrange
        VeiculoPatchRequest request = new VeiculoPatchRequest(null, 2023, null, null, null);

        // Act & Assert
        assertThat(request.ano()).isEqualTo(2023);
    }

    @Test
    @DisplayName("ano() deve aceitar null para atualização parcial")
    void ano_null_deveSerPermitido() {
        // Arrange
        VeiculoPatchRequest request = new VeiculoPatchRequest(null, null, null, null, null);

        // Act & Assert
        assertThat(request.ano()).isNull();
    }

    @Test
    @DisplayName("cor() deve retornar a cor correta")
    void cor() {
        // Arrange
        VeiculoPatchRequest request = new VeiculoPatchRequest(null, null, "Azul", null, null);

        // Act & Assert
        assertThat(request.cor()).isEqualTo("Azul");
    }

    @Test
    @DisplayName("cor() deve aceitar null para atualização parcial")
    void cor_null_deveSerPermitido() {
        // Arrange
        VeiculoPatchRequest request = new VeiculoPatchRequest(null, null, null, null, null);

        // Act & Assert
        assertThat(request.cor()).isNull();
    }

    @Test
    @DisplayName("preco() deve retornar o preço correto")
    void preco() {
        // Arrange
        VeiculoPatchRequest request = new VeiculoPatchRequest(null, null, null, new BigDecimal("50000.00"), null);

        // Act & Assert
        assertThat(request.preco()).isEqualByComparingTo(new BigDecimal("50000.00"));
    }

    @Test
    @DisplayName("preco() deve aceitar null para atualização parcial")
    void preco_null_deveSerPermitido() {
        // Arrange
        VeiculoPatchRequest request = new VeiculoPatchRequest(null, null, null, null, null);

        // Act & Assert
        assertThat(request.preco()).isNull();
    }

    @Test
    @DisplayName("placa() deve retornar a placa correta")
    void placa() {
        // Arrange
        VeiculoPatchRequest request = new VeiculoPatchRequest(null, null, null, null, "XYZ-9999");

        // Act & Assert
        assertThat(request.placa()).isEqualTo("XYZ-9999");
    }

    @Test
    @DisplayName("placa() deve aceitar null para atualização parcial")
    void placa_null_deveSerPermitido() {
        // Arrange
        VeiculoPatchRequest request = new VeiculoPatchRequest(null, null, null, null, null);

        // Act & Assert
        assertThat(request.placa()).isNull();
    }

    @Test
    @DisplayName("deve criar request com todos os campos preenchidos")
    void todosOsCamposPreenchidos() {
        // Arrange & Act
        VeiculoPatchRequest request = new VeiculoPatchRequest(
                "Toyota", 2024, "Prata", new BigDecimal("120000.00"), "ABC-1234"
        );

        // Assert
        assertThat(request.marca()).isEqualTo("Toyota");
        assertThat(request.ano()).isEqualTo(2024);
        assertThat(request.cor()).isEqualTo("Prata");
        assertThat(request.preco()).isEqualByComparingTo(new BigDecimal("120000.00"));
        assertThat(request.placa()).isEqualTo("ABC-1234");
    }
}