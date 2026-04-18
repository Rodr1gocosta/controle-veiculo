package com.rodr1gocosta.controle_veiculo.service;

import com.rodr1gocosta.controle_veiculo.dto.cotacao.CotacaoResponse;
import com.rodr1gocosta.controle_veiculo.dto.cotacao.FrankfurterResponse;
import com.rodr1gocosta.controle_veiculo.exception.CotacaoIndisponivelException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class CambioService {

    private static final String URL_AWESOME     = "https://economia.awesomeapi.com.br/json/last/USD-BRL";
    private static final String URL_FRANKFURTER = "https://api.frankfurter.dev/v1/latest?from=USD&to=BRL";
    private static final String CACHE_KEY       = "cotacao:usd-brl";

    private final RestClient restClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final ApplicationContext applicationContext;

    private CambioService self() {
        return applicationContext.getBean(CambioService.class);
    }

    public BigDecimal getCotacaoDolar() {
        // CACHE REDIS
        String cached = redisTemplate.opsForValue().get(CACHE_KEY);
        if (cached != null) {
            log.info("Cache hit Redis: {}", cached);
            return new BigDecimal(cached);
        }
        return self().buscarDaApiPrincipal();
    }

    // REQUISICAO PRINCIPAL
    @CircuitBreaker(name = "cotacaoCB", fallbackMethod = "fallbackFrankfurter")
    public BigDecimal buscarDaApiPrincipal() {
        log.info("Chamando AwesomeAPI via RestClient...");
        CotacaoResponse response = restClient.get()
                .uri(URL_AWESOME)
                .retrieve()
                .body(CotacaoResponse.class);

        assert response != null;
        BigDecimal valor = response.USDBRL().bid();
        salvarNoCache(valor);
        return valor;
    }

    // REQUISICAO SEGUNDARIA
    @CircuitBreaker(name = "cotacaoFallbackCB", fallbackMethod = "fallbackIndisponivel")
    public BigDecimal fallbackFrankfurter(Throwable e) {
        log.warn("AwesomeAPI indisponível. Acionando fallback Frankfurter. Erro: {}", e.getMessage());
        FrankfurterResponse response = restClient.get()
                .uri(URL_FRANKFURTER)
                .retrieve()
                .body(FrankfurterResponse.class);

        assert response != null;
        BigDecimal valor = response.rates().get("BRL");
        salvarNoCache(valor);
        return valor;
    }

    // CASO AS DUAS REQUISICOES FALHEM, RETORNA ERRO TRATADO
    public BigDecimal fallbackIndisponivel(Throwable e) {
        log.error("Ambas as APIs de cotação falharam. Erro: {}", e.getMessage());
        throw new CotacaoIndisponivelException();
    }

    private void salvarNoCache(BigDecimal valor) {
        redisTemplate.opsForValue().set(CACHE_KEY, valor.toPlainString(), Duration.ofMinutes(30));
        log.info("Cotação salva no Redis por 30 min: {}", valor);
    }
}
