package com.rodr1gocosta.controle_veiculo.exception;

public class CotacaoIndisponivelException extends RuntimeException {
    public CotacaoIndisponivelException() {
        super("Serviço de cotação do dólar indisponível. Tente novamente mais tarde.");
    }
}
