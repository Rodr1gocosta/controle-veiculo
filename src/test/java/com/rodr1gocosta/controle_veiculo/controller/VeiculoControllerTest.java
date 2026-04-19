package com.rodr1gocosta.controle_veiculo.controller;

import com.rodr1gocosta.controle_veiculo.dto.*;
import com.rodr1gocosta.controle_veiculo.service.CambioService;
import com.rodr1gocosta.controle_veiculo.service.VeiculoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class VeiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VeiculoService veiculoService;

    @MockitoBean
    private CambioService cambioService;

    private VeiculoResponse veiculoResponseFixture;
    private UUID veiculoId;

    @BeforeEach
    void setUp() {
        veiculoId = UUID.randomUUID();
        veiculoResponseFixture = new VeiculoResponse(
                veiculoId, "Toyota", 2022, "Prata",
                new BigDecimal("20000.00"), "ABC-1234", true,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("GET /veiculos sem token deve retornar 401")
    void listarTodos_semToken_retorna401() throws Exception {
        mockMvc.perform(get("/veiculos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /veiculos com USER deve retornar 200")
    void listarTodos() throws Exception {
        // Arrange
        when(veiculoService.listar(any())).thenReturn(new PageImpl<>(List.of(veiculoResponseFixture)));

        // Act & Assert
        mockMvc.perform(get("/veiculos")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].marca").value("Toyota"));

        verify(veiculoService).listar(any());
    }

    @Test
    @DisplayName("GET /veiculos/especificacao com filtros deve retornar 200")
    void listarPorEspecificacao() throws Exception {
        // Arrange
        when(veiculoService.listar(eq("Toyota"), eq(2022), eq("Prata"), any()))
                .thenReturn(new PageImpl<>(List.of(veiculoResponseFixture)));

        // Act & Assert
        mockMvc.perform(get("/veiculos/especificacao")
                        .param("marca", "Toyota").param("ano", "2022").param("cor", "Prata")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].marca").value("Toyota"));
    }

    @Test
    @DisplayName("GET /veiculos/preco com range deve retornar 200")
    void listarPorPreco() throws Exception {
        // Arrange
        when(veiculoService.listar(any(BigDecimal.class), any(BigDecimal.class), any()))
                .thenReturn(new PageImpl<>(List.of(veiculoResponseFixture)));

        // Act & Assert
        mockMvc.perform(get("/veiculos/preco")
                        .param("minPreco", "10000").param("maxPreco", "30000")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /veiculos/{id} deve retornar 200")
    void buscarPorId() throws Exception {
        // Arrange
        when(veiculoService.buscarPorId(veiculoId)).thenReturn(veiculoResponseFixture);

        // Act & Assert
        mockMvc.perform(get("/veiculos/{id}", veiculoId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(veiculoId.toString()))
                .andExpect(jsonPath("$.marca").value("Toyota"));
    }

    @Test
    @DisplayName("POST /veiculos com USER deve retornar 403")
    void criar_comUser_retorna403() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/veiculos")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"marca":"Toyota","ano":2022,"cor":"Prata","preco":100000,"placa":"ABC-1234"}
                        """))
                .andExpect(status().isForbidden());

        verify(veiculoService, never()).criar(any());
    }

    @Test
    @DisplayName("POST /veiculos com ADMIN deve retornar 201")
    void criar() throws Exception {
        // Arrange
        when(veiculoService.criar(any(VeiculoRequest.class))).thenReturn(veiculoResponseFixture);

        // Act & Assert
        mockMvc.perform(post("/veiculos")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"marca":"Toyota","ano":2022,"cor":"Prata","preco":100000,"placa":"ABC-1234"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.marca").value("Toyota"));

        verify(veiculoService).criar(any(VeiculoRequest.class));
    }

    @Test
    @DisplayName("PUT /veiculos/{id} com USER deve retornar 403")
    void atualizar_comUser_retorna403() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/veiculos/{id}", veiculoId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"marca":"Honda","ano":2023,"cor":"Preto","preco":50000,"placa":"XYZ-9999"}
                        """))
                .andExpect(status().isForbidden());

        verify(veiculoService, never()).atualizar(any(), any());
    }

    @Test
    @DisplayName("PUT /veiculos/{id} com ADMIN deve retornar 200")
    void atualizar() throws Exception {
        // Arrange
        when(veiculoService.atualizar(eq(veiculoId), any(VeiculoRequest.class)))
                .thenReturn(veiculoResponseFixture);

        // Act & Assert
        mockMvc.perform(put("/veiculos/{id}", veiculoId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"marca":"Honda","ano":2023,"cor":"Preto","preco":50000,"placa":"XYZ-9999"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.marca").value("Toyota"));

        verify(veiculoService).atualizar(eq(veiculoId), any(VeiculoRequest.class));
    }

    @Test
    @DisplayName("PATCH /veiculos/{id} com ADMIN deve retornar 200")
    void atualizarParcialmente() throws Exception {
        // Arrange
        when(veiculoService.atualizarParcialmente(eq(veiculoId), any(VeiculoPatchRequest.class)))
                .thenReturn(veiculoResponseFixture);

        // Act & Assert
        mockMvc.perform(patch("/veiculos/{id}", veiculoId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"marca":"Honda"}
                        """))
                .andExpect(status().isOk());

        verify(veiculoService).atualizarParcialmente(eq(veiculoId), any(VeiculoPatchRequest.class));
    }

    @Test
    @DisplayName("DELETE /veiculos/{id} com USER deve retornar 403")
    void deletar_comUser_retorna403() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/veiculos/{id}", veiculoId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());

        verify(veiculoService, never()).deletar(any());
    }

    @Test
    @DisplayName("DELETE /veiculos/{id} com ADMIN deve retornar 204")
    void deletar() throws Exception {
        // Arrange
        doNothing().when(veiculoService).deletar(veiculoId);

        // Act & Assert
        mockMvc.perform(delete("/veiculos/{id}", veiculoId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNoContent());

        verify(veiculoService).deletar(veiculoId);
    }

    @Test
    @DisplayName("GET /veiculos/relatorios/por-marca deve retornar 200")
    void relatorioPorMarca() throws Exception {
        // Arrange
        MarcaRelatorioResponse relatorio = new MarcaRelatorioResponse("Toyota", 5L);
        when(veiculoService.relatorioPorMarca(any())).thenReturn(new PageImpl<>(List.of(relatorio)));

        // Act & Assert
        mockMvc.perform(get("/veiculos/relatorios/por-marca")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].marca").value("Toyota"))
                .andExpect(jsonPath("$.content[0].quantidade").value(5));
    }
}