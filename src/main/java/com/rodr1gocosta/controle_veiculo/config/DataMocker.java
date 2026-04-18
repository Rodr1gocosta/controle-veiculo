package com.rodr1gocosta.controle_veiculo.config;

import com.rodr1gocosta.controle_veiculo.domain.Veiculo;
import com.rodr1gocosta.controle_veiculo.repository.VeiculoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataMocker {

    @Bean
    CommandLineRunner initDatabase(VeiculoRepository veiculoRepository) {
        return args -> {
            if (veiculoRepository.count() > 0) return;

            veiculoRepository.saveAll(List.of(
                Veiculo.builder()
                        .marca("Toyota")
                        .ano(2022)
                        .cor("Prata")
                        .preco(new BigDecimal("115000.00"))
                        .placa("ABC-1234")
                        .ativo(true)
                        .build(),

                Veiculo.builder()
                        .marca("Toyota")
                        .ano(2023)
                        .cor("Branco")
                        .preco(new BigDecimal("280000.00"))
                        .placa("DEF-5678")
                        .ativo(true)
                        .build(),

                Veiculo.builder()
                        .marca("Honda")
                        .ano(2021)
                        .cor("Preto")
                        .preco(new BigDecimal("105000.00"))
                        .placa("GHI-9012")
                        .ativo(false)
                        .build(),

                Veiculo.builder()
                        .marca("Honda")
                        .ano(2022)
                        .cor("Vermelho")
                        .preco(new BigDecimal("130000.00"))
                        .placa("JKL-3456")
                        .ativo(true)
                        .build(),

                Veiculo.builder()
                        .marca("Volkswagen")
                        .ano(2023)
                        .cor("Cinza")
                        .preco(new BigDecimal("210000.00"))
                        .placa("MNO-7890")
                        .ativo(true)
                        .build(),

                Veiculo.builder()
                        .marca("Volkswagen")
                        .ano(2022)
                        .cor("Azul")
                        .preco(new BigDecimal("120000.00"))
                        .placa("PQR-2345")
                        .ativo(true)
                        .build(),

                Veiculo.builder()
                        .marca("Chevrolet")
                        .ano(2023)
                        .cor("Branco")
                        .preco(new BigDecimal("85000.00"))
                        .placa("STU-6789")
                        .ativo(false)
                        .build(),

                Veiculo.builder()
                        .marca("Chevrolet")
                        .ano(2021)
                        .cor("Laranja")
                        .preco(new BigDecimal("110000.00"))
                        .placa("VWX-0123")
                        .ativo(false)
                        .build(),

                Veiculo.builder()
                        .marca("Hyundai")
                        .ano(2023)
                        .cor("Prata")
                        .preco(new BigDecimal("72000.00"))
                        .placa("YZA-4567")
                        .ativo(true)
                        .build(),

                Veiculo.builder()
                        .marca("Jeep")
                        .ano(2022)
                        .cor("Preto")
                        .preco(new BigDecimal("185000.00"))
                        .placa("BCD-8901")
                        .ativo(true)
                        .build()
            ));

            System.out.println("10 veículos de teste carregados com sucesso!");
        };
    }
}

