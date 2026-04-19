package com.rodr1gocosta.controle_veiculo.dto.cotacao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class UsdBrlTest {

    @Test
    @DisplayName("bid() deve retornar o valor da cotação correto")
    void bid() {
        // Arrange
        UsdBrl usdBrl = new UsdBrl(new BigDecimal("5.75"));

        // Act & Assert
        assertThat(usdBrl.bid()).isEqualByComparingTo(new BigDecimal("5.75"));
    }

    @Test
    @DisplayName("bid() deve aceitar valores com muitas casas decimais")
    void bid_comCasasDecimais() {
        // Arrange
        UsdBrl usdBrl = new UsdBrl(new BigDecimal("5.7523"));

        // Act & Assert
        assertThat(usdBrl.bid()).isEqualByComparingTo(new BigDecimal("5.7523"));
    }

    @Test
    @DisplayName("bid() deve aceitar null")
    void bid_null_deveSerPermitido() {
        // Arrange
        UsdBrl usdBrl = new UsdBrl(null);

        // Act & Assert
        assertThat(usdBrl.bid()).isNull();
    }

    @Test
    @DisplayName("dois registros com mesmo bid devem ser iguais")
    void igualdade_deveSerPorValorDoBid() {
        // Arrange
        UsdBrl r1 = new UsdBrl(new BigDecimal("5.75"));
        UsdBrl r2 = new UsdBrl(new BigDecimal("5.75"));

        // Act & Assert
        assertThat(r1).isEqualTo(r2);
    }

    @Test
    @DisplayName("registros com bids diferentes devem ser diferentes")
    void igualdade_comBidsDiferentes_deveSerDiferente() {
        // Arrange
        UsdBrl r1 = new UsdBrl(new BigDecimal("5.75"));
        UsdBrl r2 = new UsdBrl(new BigDecimal("6.00"));

        // Act & Assert
        assertThat(r1).isNotEqualTo(r2);
    }
}