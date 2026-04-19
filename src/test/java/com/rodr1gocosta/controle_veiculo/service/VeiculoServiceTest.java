package com.rodr1gocosta.controle_veiculo.service;

import com.rodr1gocosta.controle_veiculo.domain.Veiculo;
import com.rodr1gocosta.controle_veiculo.dto.*;
import com.rodr1gocosta.controle_veiculo.exception.PlacaDuplicadaException;
import com.rodr1gocosta.controle_veiculo.exception.VeiculoNotFoundException;
import com.rodr1gocosta.controle_veiculo.repository.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private CambioService cambioService;

    @InjectMocks
    private VeiculoService veiculoService;

    private Veiculo veiculoFixture;
    private UUID veiculoId;

    @BeforeEach
    void setUp() {
        veiculoId = UUID.randomUUID();
        veiculoFixture = Veiculo.builder()
                .id(veiculoId)
                .marca("Toyota")
                .ano(2022)
                .cor("Prata")
                .preco(new BigDecimal("20000.00"))
                .placa("ABC-1234")
                .ativo(true)
                .build();
    }

    @Test
    @DisplayName("deve retornar página de veículos ordenada por marca")
    void listar_deveRetornarPaginaOrdenadaPorMarca() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Veiculo> pageEsperada = new PageImpl<>(List.of(veiculoFixture));
        when(veiculoRepository.findAll(any(Pageable.class))).thenReturn(pageEsperada);

        // Act
        Page<VeiculoResponse> resultado = veiculoService.listar(pageable);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).marca()).isEqualTo("Toyota");
        verify(veiculoRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("deve filtrar veículos por marca, ano e cor")
    void listar_deveFiltrarPorMarcaAnoECor() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Veiculo> pageEsperada = new PageImpl<>(List.of(veiculoFixture));
        when(veiculoRepository.findByMarcaOrAnoOrCor(eq("Toyota"), eq(2022), eq("Prata"), any(Pageable.class)))
                .thenReturn(pageEsperada);

        // Act
        Page<VeiculoResponse> resultado = veiculoService.listar("Toyota", 2022, "Prata", pageable);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).marca()).isEqualTo("Toyota");
        verify(veiculoRepository).findByMarcaOrAnoOrCor(eq("Toyota"), eq(2022), eq("Prata"), any(Pageable.class));
    }

    @Test
    @DisplayName("deve filtrar veículos por marca apenas (ano e cor nulos)")
    void listar_deveFiltrarSomentePorMarca() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Veiculo> pageEsperada = new PageImpl<>(List.of(veiculoFixture));
        when(veiculoRepository.findByMarcaOrAnoOrCor(eq("Toyota"), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(pageEsperada);

        // Act
        Page<VeiculoResponse> resultado = veiculoService.listar("Toyota", null, null, pageable);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        verify(veiculoRepository).findByMarcaOrAnoOrCor(eq("Toyota"), isNull(), isNull(), any(Pageable.class));
    }

    @Test
    @DisplayName("deve filtrar veículos por ano apenas (marca e cor nulos)")
    void listar_deveFiltrarSomentePorAno() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Veiculo> pageEsperada = new PageImpl<>(List.of(veiculoFixture));
        when(veiculoRepository.findByMarcaOrAnoOrCor(isNull(), eq(2022), isNull(), any(Pageable.class)))
                .thenReturn(pageEsperada);

        // Act
        Page<VeiculoResponse> resultado = veiculoService.listar(null, 2022, null, pageable);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        verify(veiculoRepository).findByMarcaOrAnoOrCor(isNull(), eq(2022), isNull(), any(Pageable.class));
    }

    @Test
    @DisplayName("deve filtrar veículos por cor apenas (marca e ano nulos)")
    void listar_deveFiltrarSomentePorCor() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Veiculo> pageEsperada = new PageImpl<>(List.of(veiculoFixture));
        when(veiculoRepository.findByMarcaOrAnoOrCor(isNull(), isNull(), eq("Prata"), any(Pageable.class)))
                .thenReturn(pageEsperada);

        // Act
        Page<VeiculoResponse> resultado = veiculoService.listar(null, null, "Prata", pageable);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        verify(veiculoRepository).findByMarcaOrAnoOrCor(isNull(), isNull(), eq("Prata"), any(Pageable.class));
    }

    @Test
    @DisplayName("deve retornar lista vazia quando nenhum veículo combina com os filtros")
    void listar_deveRetornarVazioQuandoNenhumVeiculoCombina() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        when(veiculoRepository.findByMarcaOrAnoOrCor(eq("Ferrari"), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act
        Page<VeiculoResponse> resultado = veiculoService.listar("Ferrari", null, null, pageable);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        verify(veiculoRepository).findByMarcaOrAnoOrCor(eq("Ferrari"), isNull(), isNull(), any(Pageable.class));
    }

    @Test
    @DisplayName("deve filtrar veículos por range de preço")
    void listar_deveFiltrarPorRangeDePreco() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        BigDecimal min = new BigDecimal("10000");
        BigDecimal max = new BigDecimal("30000");
        Page<Veiculo> pageEsperada = new PageImpl<>(List.of(veiculoFixture));
        when(veiculoRepository.findAllByPrecoBetween(eq(min), eq(max), any(Pageable.class)))
                .thenReturn(pageEsperada);

        // Act
        Page<VeiculoResponse> resultado = veiculoService.listar(min, max, pageable);

        // Assert
        assertThat(resultado.getContent()).hasSize(1);
        verify(veiculoRepository).findAllByPrecoBetween(eq(min), eq(max), any(Pageable.class));
    }

    @Test
    @DisplayName("deve retornar veículo quando encontrado por ID")
    void buscarPorId_deveRetornarVeiculoQuandoEncontrado() {
        // Arrange
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculoFixture));

        // Act
        VeiculoResponse resultado = veiculoService.buscarPorId(veiculoId);

        // Assert
        assertThat(resultado.id()).isEqualTo(veiculoId);
        assertThat(resultado.marca()).isEqualTo("Toyota");
        assertThat(resultado.placa()).isEqualTo("ABC-1234");
        verify(veiculoRepository).findById(veiculoId);
    }

    @Test
    @DisplayName("deve lançar VeiculoNotFoundException quando não encontrado")
    void buscarPorId_deveLancarExcecaoQuandoNaoEncontrado() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(veiculoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> veiculoService.buscarPorId(idInexistente))
                .isInstanceOf(VeiculoNotFoundException.class);
        verify(veiculoRepository).findById(idInexistente);
    }

    @Test
    @DisplayName("deve converter preço BRL para USD ao criar veículo")
    void criar_deveConverterPrecoBrlParaUsd() {
        // Arrange
        VeiculoRequest request = new VeiculoRequest("Toyota", 2022, "Prata", new BigDecimal("100000.00"), "ABC-1234");
        when(cambioService.getCotacaoDolar()).thenReturn(new BigDecimal("5.00"));
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculoFixture);

        // Act
        VeiculoResponse resultado = veiculoService.criar(request);

        // Assert
        verify(cambioService).getCotacaoDolar();
        verify(veiculoRepository).save(argThat(v ->
                v.getPreco().compareTo(new BigDecimal("20000.00")) == 0 && // 100000 / 5 = 20000
                Boolean.TRUE.equals(v.getAtivo())
        ));
        assertThat(resultado).isNotNull();
    }

    @Test
    @DisplayName("deve atualizar todos os campos do veículo")
    void atualizar_deveAtualizarTodosOsCampos() {
        // Arrange
        VeiculoRequest request = new VeiculoRequest("Honda", 2023, "Preto", new BigDecimal("50000"), "XYZ-9999");
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculoFixture));
        when(cambioService.getCotacaoDolar()).thenReturn(new BigDecimal("5.00"));
        when(veiculoRepository.save(any())).thenReturn(veiculoFixture);

        // Act
        veiculoService.atualizar(veiculoId, request);

        // Assert
        verify(veiculoRepository).save(argThat(v ->
                v.getMarca().equals("Honda") &&
                v.getAno().equals(2023) &&
                v.getCor().equals("Preto") &&
                v.getPlaca().equals("XYZ-9999")
        ));
        verify(cambioService).getCotacaoDolar();
    }

    @Test
    @DisplayName("deve lançar exceção ao atualizar veículo inexistente")
    void atualizar_deveLancarExcecaoQuandoVeiculoNaoEncontrado() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        VeiculoRequest request = new VeiculoRequest("Honda", 2023, "Preto", new BigDecimal("50000"), "XYZ-9999");
        when(veiculoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> veiculoService.atualizar(idInexistente, request))
                .isInstanceOf(VeiculoNotFoundException.class);
        verify(veiculoRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve atualizar apenas a marca quando outros campos são null")
    void atualizarParcialmente_deveAtualizarApenasMarca() {
        // Arrange
        VeiculoPatchRequest request = new VeiculoPatchRequest("Honda", null, null, null, null);
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculoFixture));
        when(veiculoRepository.save(any())).thenReturn(veiculoFixture);

        // Act
        veiculoService.atualizarParcialmente(veiculoId, request);

        // Assert
        verify(veiculoRepository).save(argThat(v ->
                v.getMarca().equals("Honda") &&
                v.getAno().equals(2022) &&     // não alterado
                v.getCor().equals("Prata") &&  // não alterado
                v.getPlaca().equals("ABC-1234") // não alterado
        ));
        verify(cambioService, never()).getCotacaoDolar(); // preço não foi alterado
    }

    @Test
    @DisplayName("deve atualizar ano, cor e placa quando informados")
    void atualizarParcialmente_deveAtualizarAnoCorePlaca() {
        // Arrange
        VeiculoPatchRequest request = new VeiculoPatchRequest(null, 2024, "Azul", null, "NEW-5678");
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculoFixture));
        when(veiculoRepository.save(any())).thenReturn(veiculoFixture);

        // Act
        veiculoService.atualizarParcialmente(veiculoId, request);

        // Assert
        verify(veiculoRepository).save(argThat(v ->
                v.getMarca().equals("Toyota") &&    // não alterado
                v.getAno().equals(2024) &&          // alterado
                v.getCor().equals("Azul") &&        // alterado
                v.getPlaca().equals("NEW-5678") &&  // alterado
                v.getPreco().compareTo(new BigDecimal("20000.00")) == 0 // não alterado
        ));
        verify(cambioService, never()).getCotacaoDolar();
    }

    @Test
    @DisplayName("deve converter preço quando informado no PATCH")
    void atualizarParcialmente_deveConverterPrecoQuandoInformado() {
        // Arrange
        VeiculoPatchRequest request = new VeiculoPatchRequest(null, null, null, new BigDecimal("50000"), null);
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculoFixture));
        when(cambioService.getCotacaoDolar()).thenReturn(new BigDecimal("5.00"));
        when(veiculoRepository.save(any())).thenReturn(veiculoFixture);

        // Act
        veiculoService.atualizarParcialmente(veiculoId, request);

        // Assert
        verify(cambioService).getCotacaoDolar();
        verify(veiculoRepository).save(argThat(v ->
                v.getPreco().compareTo(new BigDecimal("10000.00")) == 0 // 50000 / 5 = 10000
        ));
    }

    @Test
    @DisplayName("deve fazer soft delete setando ativo=false")
    void deletar_deveFazerSoftDelete() {
        // Arrange
        when(veiculoRepository.findById(veiculoId)).thenReturn(Optional.of(veiculoFixture));
        when(veiculoRepository.save(any())).thenReturn(veiculoFixture);

        // Act
        veiculoService.deletar(veiculoId);

        // Assert
        assertThat(veiculoFixture.getAtivo()).isFalse();
        verify(veiculoRepository).save(veiculoFixture);
    }

    @Test
    @DisplayName("deve lançar exceção ao deletar veículo inexistente")
    void deletar_deveLancarExcecaoQuandoVeiculoNaoEncontrado() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(veiculoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> veiculoService.deletar(idInexistente))
                .isInstanceOf(VeiculoNotFoundException.class);
        verify(veiculoRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve retornar relatório agrupado por marca")
    void relatorioPorMarca_deveRetornarRelatorioAgrupado() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<MarcaRelatorioResponse> pageEsperada = new PageImpl<>(
                List.of(new MarcaRelatorioResponse("Toyota", 3L),
                        new MarcaRelatorioResponse("Honda", 2L))
        );
        when(veiculoRepository.countByMarca(any(Pageable.class))).thenReturn(pageEsperada);

        // Act
        Page<MarcaRelatorioResponse> resultado = veiculoService.relatorioPorMarca(pageable);

        // Assert
        assertThat(resultado.getContent()).hasSize(2);
        assertThat(resultado.getContent().get(0).marca()).isEqualTo("Toyota");
        assertThat(resultado.getContent().get(0).quantidade()).isEqualTo(3L);
        verify(veiculoRepository).countByMarca(any(Pageable.class));
    }
}