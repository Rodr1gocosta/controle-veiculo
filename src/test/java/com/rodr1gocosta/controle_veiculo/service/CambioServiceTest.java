package com.rodr1gocosta.controle_veiculo.service;

import com.rodr1gocosta.controle_veiculo.dto.cotacao.CotacaoResponse;
import com.rodr1gocosta.controle_veiculo.dto.cotacao.FrankfurterResponse;
import com.rodr1gocosta.controle_veiculo.dto.cotacao.UsdBrl;
import com.rodr1gocosta.controle_veiculo.exception.CotacaoIndisponivelException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CambioServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private CambioService cambioService;

    private static final String CACHE_KEY = "cotacao:usd-brl";

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("deve retornar cotação do cache Redis quando disponível")
    void getCotacaoDolar_deveRetornarDoCacheQuandoDisponivel() {
        // Arrange
        String cotacaoCached = "5.75";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(CACHE_KEY)).thenReturn(cotacaoCached);

        // Act
        BigDecimal resultado = cambioService.getCotacaoDolar();

        // Assert
        assertThat(resultado).isEqualByComparingTo(new BigDecimal("5.75"));
        verify(valueOperations).get(CACHE_KEY);
        verify(applicationContext, never()).getBean(CambioService.class);
    }


    @Test
    @DisplayName("deve buscar da API quando cache está vazio")
    void getCotacaoDolar_deveBuscarDaApiQuandoCacheVazio() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(CACHE_KEY)).thenReturn(null);
        when(applicationContext.getBean(CambioService.class)).thenReturn(cambioService);

        UsdBrl usdBrl = new UsdBrl(new BigDecimal("5.80"));
        CotacaoResponse cotacaoResponse = new CotacaoResponse(usdBrl);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CotacaoResponse.class)).thenReturn(cotacaoResponse);

        // Act
        BigDecimal resultado = cambioService.getCotacaoDolar();

        // Assert
        assertThat(resultado).isEqualByComparingTo(new BigDecimal("5.80"));
        verify(valueOperations).get(CACHE_KEY);
        verify(applicationContext).getBean(CambioService.class);
        verify(valueOperations).set(eq(CACHE_KEY), eq("5.80"), eq(Duration.ofMinutes(30)));
    }

    @Test
    @DisplayName("deve buscar cotação da AwesomeAPI e salvar no cache")
    void buscarDaApiPrincipal_deveBuscarEsalvarNoCache() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        UsdBrl usdBrl = new UsdBrl(new BigDecimal("5.85"));
        CotacaoResponse cotacaoResponse = new CotacaoResponse(usdBrl);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CotacaoResponse.class)).thenReturn(cotacaoResponse);

        // Act
        BigDecimal resultado = cambioService.buscarDaApiPrincipal();

        // Assert
        assertThat(resultado).isEqualByComparingTo(new BigDecimal("5.85"));
        verify(valueOperations).set(eq(CACHE_KEY), eq("5.85"), eq(Duration.ofMinutes(30)));
    }

    @Test
    @DisplayName("deve buscar da Frankfurter API no fallback")
    void fallbackFrankfurter_deveBuscarDaFrankfurterApi() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Map<String, BigDecimal> rates = Map.of("BRL", new BigDecimal("5.90"));
        FrankfurterResponse frankfurterResponse = new FrankfurterResponse(rates);
        Throwable exception = new RuntimeException("AwesomeAPI falhou");

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(FrankfurterResponse.class)).thenReturn(frankfurterResponse);

        // Act
        BigDecimal resultado = cambioService.fallbackFrankfurter(exception);

        // Assert
        assertThat(resultado).isEqualByComparingTo(new BigDecimal("5.90"));
        verify(valueOperations).set(eq(CACHE_KEY), eq("5.90"), eq(Duration.ofMinutes(30)));
    }

    @Test
    @DisplayName("deve lançar CotacaoIndisponivelException quando ambas APIs falharem")
    void fallbackIndisponivel_deveLancarExcecaoQuandoAmbasFalharem() {
        // Arrange
        Throwable exception = new RuntimeException("Ambas APIs falharam");

        // Act & Assert
        assertThatThrownBy(() -> cambioService.fallbackIndisponivel(exception))
                .isInstanceOf(CotacaoIndisponivelException.class)
                .hasMessage("Serviço de cotação do dólar indisponível. Tente novamente mais tarde.");
    }

    @Test
    @DisplayName("deve salvar cotação no Redis")
    void salvarNoCache_deveSalvarComTTL30Minutos() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        BigDecimal cotacao = new BigDecimal("5.95");
        UsdBrl usdBrl = new UsdBrl(cotacao);
        CotacaoResponse cotacaoResponse = new CotacaoResponse(usdBrl);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CotacaoResponse.class)).thenReturn(cotacaoResponse);

        // Act
        cambioService.buscarDaApiPrincipal();

        // Assert
        verify(valueOperations).set(
                eq(CACHE_KEY),
                eq("5.95"),
                eq(Duration.ofMinutes(30))
        );
    }

    @Test
    @DisplayName("deve retornar BigDecimal com 2 casas decimais")
    void getCotacaoDolar_deveRetornarComDuasCasasDecimais() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        String cotacaoCached = "5.8567";
        when(valueOperations.get(CACHE_KEY)).thenReturn(cotacaoCached);

        // Act
        BigDecimal resultado = cambioService.getCotacaoDolar();

        // Assert
        assertThat(resultado.scale()).isGreaterThanOrEqualTo(2);
        assertThat(resultado.toPlainString()).isEqualTo("5.8567");
    }

    @Test
    @DisplayName("deve lançar AssertionError quando AwesomeAPI retorna null")
    void buscarDaApiPrincipal_deveLancarExcecaoQuandoResponseNull() {
        // Arrange
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CotacaoResponse.class)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> cambioService.buscarDaApiPrincipal())
                .isInstanceOf(AssertionError.class);
    }

    @Test
    @DisplayName("deve lançar AssertionError quando Frankfurter retorna null")
    void fallbackFrankfurter_deveLancarExcecaoQuandoResponseNull() {
        // Arrange
        Throwable exception = new RuntimeException("AwesomeAPI falhou");
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(FrankfurterResponse.class)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> cambioService.fallbackFrankfurter(exception))
                .isInstanceOf(AssertionError.class);
    }
}