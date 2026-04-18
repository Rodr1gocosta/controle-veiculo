package com.rodr1gocosta.controle_veiculo.dto;

import java.math.BigDecimal;

public record VeiculoPatchRequest(
        String marca,
        Integer ano,
        String cor,
        BigDecimal preco,
        String placa
) {}

