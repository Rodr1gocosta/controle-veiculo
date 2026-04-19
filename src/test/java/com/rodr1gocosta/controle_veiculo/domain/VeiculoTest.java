package com.rodr1gocosta.controle_veiculo.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VeiculoTest {

    private Veiculo veiculo;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        veiculo = Veiculo.builder()
                .id(id)
                .marca("Toyota")
                .ano(2022)
                .cor("Prata")
                .preco(new BigDecimal("115000.00"))
                .placa("ABC-1234")
                .ativo(true)
                .build();
    }

    @Test
    @DisplayName("deve retornar o id correto")
    void getId() {
        assertThat(veiculo.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("deve retornar a marca correta")
    void getMarca() {
        assertThat(veiculo.getMarca()).isEqualTo("Toyota");
    }

    @Test
    @DisplayName("deve retornar o ano correto")
    void getAno() {
        assertThat(veiculo.getAno()).isEqualTo(2022);
    }

    @Test
    @DisplayName("deve retornar a cor correta")
    void getCor() {
        assertThat(veiculo.getCor()).isEqualTo("Prata");
    }

    @Test
    @DisplayName("deve retornar o preço correto")
    void getPreco() {
        assertThat(veiculo.getPreco()).isEqualByComparingTo(new BigDecimal("115000.00"));
    }

    @Test
    @DisplayName("deve retornar a placa correta")
    void getPlaca() {
        assertThat(veiculo.getPlaca()).isEqualTo("ABC-1234");
    }

    @Test
    @DisplayName("deve retornar ativo como true")
    void getAtivo() {
        assertThat(veiculo.getAtivo()).isTrue();
    }

    @Test
    @DisplayName("deve retornar created como null quando não persistido")
    void getCreated() {
        assertThat(veiculo.getCreated()).isNull();
    }

    @Test
    @DisplayName("deve retornar updated como null quando não persistido")
    void getUpdated() {
        assertThat(veiculo.getUpdated()).isNull();
    }

    @Test
    @DisplayName("deve alterar o id via setter")
    void setId() {
        // Arrange
        UUID novoId = UUID.randomUUID();

        // Act
        veiculo.setId(novoId);

        // Assert
        assertThat(veiculo.getId()).isEqualTo(novoId);
    }

    @Test
    @DisplayName("deve alterar a marca via setter")
    void setMarca() {
        // Act
        veiculo.setMarca("Honda");

        // Assert
        assertThat(veiculo.getMarca()).isEqualTo("Honda");
    }

    @Test
    @DisplayName("deve alterar o ano via setter")
    void setAno() {
        // Act
        veiculo.setAno(2024);

        // Assert
        assertThat(veiculo.getAno()).isEqualTo(2024);
    }

    @Test
    @DisplayName("deve alterar a cor via setter")
    void setCor() {
        // Act
        veiculo.setCor("Azul");

        // Assert
        assertThat(veiculo.getCor()).isEqualTo("Azul");
    }

    @Test
    @DisplayName("deve alterar o preço via setter")
    void setPreco() {
        // Arrange
        BigDecimal novoPreco = new BigDecimal("200000.00");

        // Act
        veiculo.setPreco(novoPreco);

        // Assert
        assertThat(veiculo.getPreco()).isEqualByComparingTo(novoPreco);
    }

    @Test
    @DisplayName("deve alterar a placa via setter")
    void setPlaca() {
        // Act
        veiculo.setPlaca("XYZ-9999");

        // Assert
        assertThat(veiculo.getPlaca()).isEqualTo("XYZ-9999");
    }

    @Test
    @DisplayName("deve alterar ativo para false via setter (soft delete)")
    void setAtivo() {
        // Act
        veiculo.setAtivo(false);

        // Assert
        assertThat(veiculo.getAtivo()).isFalse();
    }

    @Test
    @DisplayName("deve alterar created via setter")
    void setCreated() {
        // Arrange
        LocalDateTime agora = LocalDateTime.now();

        // Act
        veiculo.setCreated(agora);

        // Assert
        assertThat(veiculo.getCreated()).isEqualTo(agora);
    }

    @Test
    @DisplayName("deve alterar updated via setter")
    void setUpdated() {
        // Arrange
        LocalDateTime agora = LocalDateTime.now();

        // Act
        veiculo.setUpdated(agora);

        // Assert
        assertThat(veiculo.getUpdated()).isEqualTo(agora);
    }

    @Test
    @DisplayName("deve criar veículo via builder com todos os campos")
    void builder() {
        // Arrange
        UUID novoId = UUID.randomUUID();

        // Act
        Veiculo v = Veiculo.builder()
                .id(novoId)
                .marca("Volkswagen")
                .ano(2023)
                .cor("Branco")
                .preco(new BigDecimal("90000.00"))
                .placa("DEF-5678")
                .ativo(true)
                .build();

        // Assert
        assertThat(v.getId()).isEqualTo(novoId);
        assertThat(v.getMarca()).isEqualTo("Volkswagen");
        assertThat(v.getAno()).isEqualTo(2023);
        assertThat(v.getCor()).isEqualTo("Branco");
        assertThat(v.getPreco()).isEqualByComparingTo(new BigDecimal("90000.00"));
        assertThat(v.getPlaca()).isEqualTo("DEF-5678");
        assertThat(v.getAtivo()).isTrue();
    }
}