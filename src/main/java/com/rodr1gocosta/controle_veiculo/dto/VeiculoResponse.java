package com.rodr1gocosta.controle_veiculo.dto;

import com.rodr1gocosta.controle_veiculo.domain.Veiculo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record VeiculoResponse(
        UUID id,
        String marca,
        Integer ano,
        String cor,
        BigDecimal preco,
        String placa,
        Boolean ativo,
        LocalDateTime created,
        LocalDateTime updated
) {
    public static VeiculoResponse from(Veiculo veiculo) {
        return new VeiculoResponse(
                veiculo.getId(),
                veiculo.getMarca(),
                veiculo.getAno(),
                veiculo.getCor(),
                veiculo.getPreco(),
                veiculo.getPlaca(),
                veiculo.getAtivo(),
                veiculo.getCreated(),
                veiculo.getUpdated()
        );
    }
}

