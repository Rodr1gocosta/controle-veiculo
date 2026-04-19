package com.rodr1gocosta.controle_veiculo.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CotacaoIndisponivelExceptionTest {

    @Test
    @DisplayName("deve criar exceção com mensagem padrão")
    void deveCriarExcecaoComMensagemPadrao() {
        // Act
        CotacaoIndisponivelException exception = new CotacaoIndisponivelException();

        // Assert
        assertThat(exception.getMessage())
                .isEqualTo("Serviço de cotação do dólar indisponível. Tente novamente mais tarde.");
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("deve ser uma RuntimeException")
    void deveSerRuntimeException() {
        // Act
        CotacaoIndisponivelException exception = new CotacaoIndisponivelException();

        // Assert
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}