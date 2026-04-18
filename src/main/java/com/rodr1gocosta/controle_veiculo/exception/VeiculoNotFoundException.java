package com.rodr1gocosta.controle_veiculo.exception;

import java.util.UUID;

public class VeiculoNotFoundException extends RuntimeException {
    public VeiculoNotFoundException(UUID id) {
        super("Veículo não encontrado com id: " + id);
    }
}

