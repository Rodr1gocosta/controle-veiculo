package com.rodr1gocosta.controle_veiculo.dto.cotacao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CotacaoResponseTest {

    @Test
    @DisplayName("USDBRL() deve retornar o objeto UsdBrl correto")
    void USDBRL() {
        // Arrange
        UsdBrl usdBrl = new UsdBrl(new BigDecimal("5.75"));
        CotacaoResponse response = new CotacaoResponse(usdBrl);

        // Act & Assert
        assertThat(response.USDBRL()).isEqualTo(usdBrl);
        assertThat(response.USDBRL().bid()).isEqualByComparingTo(new BigDecimal("5.75"));
    }

    @Test
    @DisplayName("USDBRL() deve permitir acesso ao bid via encadeamento")
    void USDBRL_devePermitirAcessoAoBid() {
        // Arrange
        BigDecimal cotacao = new BigDecimal("5.9823");
        CotacaoResponse response = new CotacaoResponse(new UsdBrl(cotacao));

        // Act
        BigDecimal bid = response.USDBRL().bid();

        // Assert
        assertThat(bid).isEqualByComparingTo(cotacao);
    }

    @Test
    @DisplayName("USDBRL() deve aceitar null")
    void USDBRL_null_deveSerPermitido() {
        // Arrange
        CotacaoResponse response = new CotacaoResponse(null);

        // Act & Assert
        assertThat(response.USDBRL()).isNull();
    }

    @Test
    @DisplayName("dois registros com mesmo USDBRL devem ser iguais")
    void igualdade_deveSerPorValor() {
        // Arrange
        UsdBrl usdBrl = new UsdBrl(new BigDecimal("5.75"));
        CotacaoResponse r1 = new CotacaoResponse(usdBrl);
        CotacaoResponse r2 = new CotacaoResponse(usdBrl);

        // Act & Assert
        assertThat(r1).isEqualTo(r2);
    }

    @Test
    @DisplayName("registros com UsdBrl diferentes devem ser diferentes")
    void igualdade_comUsdBrlDiferentes_deveSerDiferente() {
        // Arrange
        CotacaoResponse r1 = new CotacaoResponse(new UsdBrl(new BigDecimal("5.75")));
        CotacaoResponse r2 = new CotacaoResponse(new UsdBrl(new BigDecimal("6.00")));

        // Act & Assert
        assertThat(r1).isNotEqualTo(r2);
    }
}