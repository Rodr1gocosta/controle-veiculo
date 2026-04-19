package com.rodr1gocosta.controle_veiculo.dto.cotacao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FrankfurterResponseTest {

    @Test
    @DisplayName("rates() deve retornar o mapa de taxas correto")
    void rates() {
        // Arrange
        Map<String, BigDecimal> rates = Map.of("BRL", new BigDecimal("5.90"));
        FrankfurterResponse response = new FrankfurterResponse(rates);

        // Act & Assert
        assertThat(response.rates()).containsKey("BRL");
        assertThat(response.rates().get("BRL")).isEqualByComparingTo(new BigDecimal("5.90"));
    }

    @Test
    @DisplayName("rates() deve retornar a taxa BRL correta para conversão")
    void rates_deveTerValorBRL() {
        // Arrange
        BigDecimal cotacaoBRL = new BigDecimal("5.7523");
        FrankfurterResponse response = new FrankfurterResponse(Map.of("BRL", cotacaoBRL));

        // Act
        BigDecimal resultado = response.rates().get("BRL");

        // Assert
        assertThat(resultado).isEqualByComparingTo(cotacaoBRL);
    }

    @Test
    @DisplayName("rates() deve aceitar múltiplas moedas")
    void rates_comMultiplasMoedas() {
        // Arrange
        Map<String, BigDecimal> rates = Map.of(
                "BRL", new BigDecimal("5.90"),
                "EUR", new BigDecimal("0.92"),
                "GBP", new BigDecimal("0.79")
        );
        FrankfurterResponse response = new FrankfurterResponse(rates);

        // Act & Assert
        assertThat(response.rates()).hasSize(3);
        assertThat(response.rates()).containsKeys("BRL", "EUR", "GBP");
    }

    @Test
    @DisplayName("rates() deve aceitar null")
    void rates_null_deveSerPermitido() {
        // Arrange
        FrankfurterResponse response = new FrankfurterResponse(null);

        // Act & Assert
        assertThat(response.rates()).isNull();
    }

    @Test
    @DisplayName("dois registros com mesmo rates devem ser iguais")
    void igualdade_deveSerPorValor() {
        // Arrange
        Map<String, BigDecimal> rates = Map.of("BRL", new BigDecimal("5.90"));
        FrankfurterResponse r1 = new FrankfurterResponse(rates);
        FrankfurterResponse r2 = new FrankfurterResponse(rates);

        // Act & Assert
        assertThat(r1).isEqualTo(r2);
    }
}