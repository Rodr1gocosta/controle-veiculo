package com.rodr1gocosta.controle_veiculo.dto.cotacao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CotacaoResponse(@JsonProperty("USDBRL") UsdBrl USDBRL) { }
