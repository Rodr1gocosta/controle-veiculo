package com.rodr1gocosta.controle_veiculo.dto;

import com.rodr1gocosta.controle_veiculo.domain.Veiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VeiculoResponseTest {

    private Veiculo veiculo;
    private UUID id;
    private LocalDateTime created;
    private LocalDateTime updated;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        created = LocalDateTime.of(2024, 1, 10, 10, 0);
        updated = LocalDateTime.of(2024, 6, 15, 12, 30);

        veiculo = Veiculo.builder()
                .id(id)
                .marca("Toyota")
                .ano(2022)
                .cor("Prata")
                .preco(new BigDecimal("115000.00"))
                .placa("ABC-1234")
                .ativo(true)
                .build();

        veiculo.setCreated(created);
        veiculo.setUpdated(updated);
    }

    @Test
    @DisplayName("from() deve mapear todos os campos do Veiculo corretamente")
    void from() {
        // Act
        VeiculoResponse response = VeiculoResponse.from(veiculo);

        // Assert
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.marca()).isEqualTo("Toyota");
        assertThat(response.ano()).isEqualTo(2022);
        assertThat(response.cor()).isEqualTo("Prata");
        assertThat(response.preco()).isEqualByComparingTo(new BigDecimal("115000.00"));
        assertThat(response.placa()).isEqualTo("ABC-1234");
        assertThat(response.ativo()).isTrue();
        assertThat(response.created()).isEqualTo(created);
        assertThat(response.updated()).isEqualTo(updated);
    }

    @Test
    @DisplayName("id() deve retornar o UUID correto")
    void id() {
        // Arrange
        VeiculoResponse response = VeiculoResponse.from(veiculo);

        // Act & Assert
        assertThat(response.id()).isEqualTo(id);
    }

    @Test
    @DisplayName("marca() deve retornar a marca correta")
    void marca() {
        // Arrange
        VeiculoResponse response = VeiculoResponse.from(veiculo);

        // Act & Assert
        assertThat(response.marca()).isEqualTo("Toyota");
    }

    @Test
    @DisplayName("ano() deve retornar o ano correto")
    void ano() {
        // Arrange
        VeiculoResponse response = VeiculoResponse.from(veiculo);

        // Act & Assert
        assertThat(response.ano()).isEqualTo(2022);
    }

    @Test
    @DisplayName("cor() deve retornar a cor correta")
    void cor() {
        // Arrange
        VeiculoResponse response = VeiculoResponse.from(veiculo);

        // Act & Assert
        assertThat(response.cor()).isEqualTo("Prata");
    }

    @Test
    @DisplayName("preco() deve retornar o preço correto")
    void preco() {
        // Arrange
        VeiculoResponse response = VeiculoResponse.from(veiculo);

        // Act & Assert
        assertThat(response.preco()).isEqualByComparingTo(new BigDecimal("115000.00"));
    }

    @Test
    @DisplayName("placa() deve retornar a placa correta")
    void placa() {
        // Arrange
        VeiculoResponse response = VeiculoResponse.from(veiculo);

        // Act & Assert
        assertThat(response.placa()).isEqualTo("ABC-1234");
    }

    @Test
    @DisplayName("ativo() deve retornar true")
    void ativo() {
        // Arrange
        VeiculoResponse response = VeiculoResponse.from(veiculo);

        // Act & Assert
        assertThat(response.ativo()).isTrue();
    }

    @Test
    @DisplayName("created() deve retornar a data de criação correta")
    void created() {
        // Arrange
        VeiculoResponse response = VeiculoResponse.from(veiculo);

        // Act & Assert
        assertThat(response.created()).isEqualTo(created);
    }

    @Test
    @DisplayName("updated() deve retornar a data de atualização correta")
    void updated() {
        // Arrange
        VeiculoResponse response = VeiculoResponse.from(veiculo);

        // Act & Assert
        assertThat(response.updated()).isEqualTo(updated);
    }
}