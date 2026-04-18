package com.rodr1gocosta.controle_veiculo.dto.cotacao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FrankfurterResponse(Map<String, BigDecimal> rates) {}

