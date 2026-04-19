package com.rodr1gocosta.controle_veiculo.service;

import com.rodr1gocosta.controle_veiculo.dto.*;
import com.rodr1gocosta.controle_veiculo.domain.Veiculo;
import com.rodr1gocosta.controle_veiculo.exception.PlacaDuplicadaException;
import com.rodr1gocosta.controle_veiculo.exception.VeiculoNotFoundException;
import com.rodr1gocosta.controle_veiculo.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final CambioService cambioService;

    public Page<VeiculoResponse> listar(Pageable pageable) {
        Pageable p = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("marca").ascending());
        return veiculoRepository.findAll(p).map(VeiculoResponse::from);
    }

    public Page<VeiculoResponse> listar(String marca, Integer ano, String cor, Pageable pageable) {
        Pageable p = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("marca").ascending());
        return veiculoRepository.findByMarcaOrAnoOrCor(marca, ano, cor, p).map(VeiculoResponse::from);
    }

    public Page<VeiculoResponse> listar(BigDecimal minPreco, BigDecimal maxPreco, Pageable pageable) {
        Pageable p = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("preco").ascending());
        return veiculoRepository.findAllByPrecoBetween(minPreco, maxPreco, p).map(VeiculoResponse::from);
    }

    public VeiculoResponse buscarPorId(UUID id) {
        return veiculoRepository.findById(id)
                .map(VeiculoResponse::from)
                .orElseThrow(() -> new VeiculoNotFoundException(id));
    }

    @Transactional
    public VeiculoResponse criar(VeiculoRequest request) {
        if (veiculoRepository.existsByPlaca(request.placa())) {
            throw new PlacaDuplicadaException(request.placa());
        }

        Veiculo veiculo = Veiculo.builder()
                .marca(request.marca())
                .ano(request.ano())
                .cor(request.cor())
                .preco(calculaPrecoEmDolar(request.preco()))
                .placa(request.placa())
                .ativo(true)
                .build();

        return VeiculoResponse.from(veiculoRepository.save(veiculo));
    }

    @Transactional
    public VeiculoResponse atualizar(UUID id, VeiculoRequest request) {
        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new VeiculoNotFoundException(id));

        if (veiculoRepository.existsByPlacaAndIdNot(request.placa(), id)) {
            throw new PlacaDuplicadaException(request.placa());
        }

        veiculo.setMarca(request.marca());
        veiculo.setAno(request.ano());
        veiculo.setCor(request.cor());
        veiculo.setPreco(calculaPrecoEmDolar(request.preco()));
        veiculo.setPlaca(request.placa());

        return VeiculoResponse.from(veiculoRepository.save(veiculo));
    }

    @Transactional
    public VeiculoResponse atualizarParcialmente(UUID id, VeiculoPatchRequest request) {
        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new VeiculoNotFoundException(id));

        if (request.marca() != null)    veiculo.setMarca(request.marca());
        if (request.ano() != null)      veiculo.setAno(request.ano());
        if (request.cor() != null)      veiculo.setCor(request.cor());
        if (request.preco() != null)    veiculo.setPreco(calculaPrecoEmDolar(request.preco()));
        if (request.placa() != null) {
            if (veiculoRepository.existsByPlacaAndIdNot(request.placa(), id)) {
                throw new PlacaDuplicadaException(request.placa());
            }
            veiculo.setPlaca(request.placa());
        }

        return VeiculoResponse.from(veiculoRepository.save(veiculo));
    }

    @Transactional
    public void deletar(UUID id) {
        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new VeiculoNotFoundException(id));
        veiculo.setAtivo(false);
        veiculoRepository.save(veiculo);
    }

    public Page<MarcaRelatorioResponse> relatorioPorMarca(Pageable pageable) {
        Pageable p = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("marca").ascending());
        return veiculoRepository.countByMarca(p);
    }

    private BigDecimal calculaPrecoEmDolar (BigDecimal precoReal) {
        BigDecimal cotacao = cambioService.getCotacaoDolar();
        BigDecimal precoEmDolar = precoReal.divide(cotacao, 2, java.math.RoundingMode.HALF_UP);
        log.info("Preço BRL: {} | Cotação: {} | Preço USD: {}", precoReal, cotacao, precoEmDolar);

        return precoEmDolar;
    }
}

