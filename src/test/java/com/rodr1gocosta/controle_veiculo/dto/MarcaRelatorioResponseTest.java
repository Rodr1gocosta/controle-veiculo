package com.rodr1gocosta.controle_veiculo.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MarcaRelatorioResponseTest {

    @Test
    @DisplayName("marca() deve retornar a marca correta")
    void marca() {
        // Arrange
        MarcaRelatorioResponse response = new MarcaRelatorioResponse("Toyota", 5L);

        // Act & Assert
        assertThat(response.marca()).isEqualTo("Toyota");
    }

    @Test
    @DisplayName("quantidade() deve retornar a quantidade correta")
    void quantidade() {
        // Arrange
        MarcaRelatorioResponse response = new MarcaRelatorioResponse("Toyota", 5L);

        // Act & Assert
        assertThat(response.quantidade()).isEqualTo(5L);
    }

    @Test
    @DisplayName("deve criar relatório com marca e quantidade zerada")
    void quantidade_zero() {
        // Arrange
        MarcaRelatorioResponse response = new MarcaRelatorioResponse("Honda", 0L);

        // Act & Assert
        assertThat(response.marca()).isEqualTo("Honda");
        assertThat(response.quantidade()).isZero();
    }

    @Test
    @DisplayName("dois registros com mesma marca e quantidade devem ser iguais")
    void igualdade_deveSerPorValor() {
        // Arrange
        MarcaRelatorioResponse r1 = new MarcaRelatorioResponse("Volkswagen", 3L);
        MarcaRelatorioResponse r2 = new MarcaRelatorioResponse("Volkswagen", 3L);

        // Act & Assert
        assertThat(r1).isEqualTo(r2);
    }

    @Test
    @DisplayName("registros com marcas diferentes devem ser diferentes")
    void igualdade_comMarcasDiferentes_deveSerDiferente() {
        // Arrange
        MarcaRelatorioResponse r1 = new MarcaRelatorioResponse("Toyota", 3L);
        MarcaRelatorioResponse r2 = new MarcaRelatorioResponse("Honda", 3L);

        // Act & Assert
        assertThat(r1).isNotEqualTo(r2);
    }
}