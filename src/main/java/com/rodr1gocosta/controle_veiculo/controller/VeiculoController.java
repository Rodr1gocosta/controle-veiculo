package com.rodr1gocosta.controle_veiculo.controller;

import com.rodr1gocosta.controle_veiculo.dto.VeiculoResponse;
import com.rodr1gocosta.controle_veiculo.dto.VeiculoRequest;
import com.rodr1gocosta.controle_veiculo.dto.VeiculoPatchRequest;
import com.rodr1gocosta.controle_veiculo.dto.MarcaRelatorioResponse;
import com.rodr1gocosta.controle_veiculo.service.VeiculoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/veiculos")
@RequiredArgsConstructor
public class VeiculoController {

    private final VeiculoService veiculoService;

    @GetMapping
    public ResponseEntity<Page<VeiculoResponse>> listarTodos(@PageableDefault() Pageable pageable) {
        log.info("GET /veiculos - listando todos os veículos");
        return ResponseEntity.ok(veiculoService.listar(pageable));
    }

    @GetMapping("/especificacao")
    public ResponseEntity<Page<VeiculoResponse>> listarPorEspecificacao(
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) String cor,
            @PageableDefault Pageable pageable) {
        log.info("GET /especificacao - listando todos os veículos por especificacao");
        return ResponseEntity.ok(veiculoService.listar(marca, ano, cor, pageable));
    }

    @GetMapping("/preco")
    public ResponseEntity<Page<VeiculoResponse>> listarPorPreco(
            @RequestParam(required = false) BigDecimal minPreco,
            @RequestParam(required = false) BigDecimal maxPreco,
            @PageableDefault Pageable pageable) {
        log.info("GET /preco - listando todos os veículos por preco");
        return ResponseEntity.ok(veiculoService.listar(minPreco, maxPreco, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeiculoResponse> buscarPorId(@PathVariable UUID id) {
        log.info("GET /id - buscar veiculos por id");
        return ResponseEntity.ok(veiculoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<VeiculoResponse> criar(@Valid @RequestBody VeiculoRequest request) {
        log.info("POST - salvar veiculo");

        VeiculoResponse response = veiculoService.criar(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VeiculoResponse> atualizar(@PathVariable UUID id,
                                                      @Valid @RequestBody VeiculoRequest request) {
        log.info("PUT - atualizar veiculo");
        return ResponseEntity.ok(veiculoService.atualizar(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VeiculoResponse> atualizarParcialmente(@PathVariable UUID id,
                                                                   @RequestBody VeiculoPatchRequest request) {
        log.info("PATCH - atualizar veiculo por unidade");
        return ResponseEntity.ok(veiculoService.atualizarParcialmente(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        log.info("DELETE - deletar veiculo");
        veiculoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/relatorios/por-marca")
    public ResponseEntity<Page<MarcaRelatorioResponse>> relatorioPorMarca(@PageableDefault Pageable pageable) {
        log.info("GET /relatorios/por-marca - gerar relatorio por marca");
        return ResponseEntity.ok(veiculoService.relatorioPorMarca(pageable));
    }
}

