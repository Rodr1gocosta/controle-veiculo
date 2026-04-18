package com.rodr1gocosta.controle_veiculo.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record VeiculoRequest(
        @NotBlank(message = "Marca é obrigatória")
        String marca,

        @NotNull(message = "Ano é obrigatório")
        @Min(value = 1886, message = "Ano inválido")
        @Max(value = 2100, message = "Ano inválido")
        Integer ano,

        @NotBlank(message = "Cor é obrigatória")
        String cor,

        @NotNull(message = "Preço é obrigatório")
        @DecimalMin(value = "0.0", inclusive = false, message = "Preço deve ser maior que zero")
        BigDecimal preco,

        @NotBlank(message = "Placa é obrigatória")
        String placa

) {}

