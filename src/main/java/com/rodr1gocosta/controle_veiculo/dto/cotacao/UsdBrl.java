package com.rodr1gocosta.controle_veiculo.dto.cotacao;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record UsdBrl(@JsonProperty("bid") BigDecimal bid) { }
