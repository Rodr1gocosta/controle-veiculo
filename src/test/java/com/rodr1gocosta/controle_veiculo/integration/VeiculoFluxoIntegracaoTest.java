package com.rodr1gocosta.controle_veiculo.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rodr1gocosta.controle_veiculo.service.CambioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class VeiculoFluxoIntegracaoTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CambioService cambioService;

    private String tokenAdmin;
    private String tokenUser;

    @BeforeEach
    void setUp() throws Exception {
        when(cambioService.getCotacaoDolar()).thenReturn(new BigDecimal("5.00"));
        tokenAdmin = obterToken("admin", "admin123");
        tokenUser  = obterToken("user", "user123");
    }

    // ----------------------------------------------------------------
    // HELPER: obtém JWT via POST /auth/login
    // ----------------------------------------------------------------
    private String obterToken(String username, String password) throws Exception {
        String body = """
                {"username": "%s", "password": "%s"}
                """.formatted(username, password);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("token").asText();
    }

    // ----------------------------------------------------------------
    // 1. Obter token
    // ----------------------------------------------------------------
    @Test
    @DisplayName("deve obter token JWT para ADMIN com credenciais válidas")
    void deveObterTokenAdmin() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"username":"admin","password":"admin123"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(28800));
    }

    @Test
    @DisplayName("ADMIN deve criar veículo e receber 201 com Location")
    void adminDeveCriarVeiculo() throws Exception {
        mockMvc.perform(post("/veiculos")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "marca": "Toyota",
                              "ano": 2022,
                              "cor": "Prata",
                              "preco": 100000,
                              "placa": "INT-0001"
                            }
                        """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.marca").value("Toyota"))
                .andExpect(jsonPath("$.placa").value("INT-0001"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @DisplayName("USER não deve criar veículo — deve receber 403")
    void userNaoDeveCriarVeiculo() throws Exception {
        mockMvc.perform(post("/veiculos")
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "marca": "Honda",
                              "ano": 2023,
                              "cor": "Preto",
                              "preco": 80000,
                              "placa": "INT-0002"
                            }
                        """))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("deve retornar 409 ao criar veículo com placa duplicada")
    void deveRetornar409ComPlacaDuplicada() throws Exception {
        // Cria o primeiro veículo
        mockMvc.perform(post("/veiculos")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "marca": "Toyota",
                              "ano": 2022,
                              "cor": "Prata",
                              "preco": 100000,
                              "placa": "DUP-9999"
                            }
                        """))
                .andExpect(status().isCreated());

        // Tenta criar com a mesma placa
        mockMvc.perform(post("/veiculos")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "marca": "Honda",
                              "ano": 2023,
                              "cor": "Preto",
                              "preco": 80000,
                              "placa": "DUP-9999"
                            }
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Placa duplicada"))
                .andExpect(jsonPath("$.detail").value("Já existe um veículo cadastrado com a placa: DUP-9999"));
    }

    @Test
    @DisplayName("deve listar todos os veículos com paginação")
    void deveListarTodosOsVeiculos() throws Exception {
        // Cria um veículo antes
        mockMvc.perform(post("/veiculos")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"marca":"Toyota","ano":2022,"cor":"Prata","preco":100000,"placa":"LST-0001"}
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/veiculos")
                        .header("Authorization", "Bearer " + tokenUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").isNumber())
                .andExpect(jsonPath("$.size").isNumber());
    }

    @Test
    @DisplayName("deve filtrar veículos por marca")
    void deveFiltrarVeiculosPorMarca() throws Exception {
        mockMvc.perform(post("/veiculos")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"marca":"Honda","ano":2021,"cor":"Preto","preco":90000,"placa":"FLT-0001"}
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/veiculos/especificacao")
                        .param("marca", "Honda")
                        .header("Authorization", "Bearer " + tokenUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].marca").value("Honda"));
    }

    @Test
    @DisplayName("deve filtrar veículos por range de preço")
    void deveFiltrarVeiculosPorPreco() throws Exception {
        mockMvc.perform(post("/veiculos")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"marca":"Chevrolet","ano":2020,"cor":"Branco","preco":50000,"placa":"PRC-0001"}
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/veiculos/preco")
                        .param("minPreco", "5000")
                        .param("maxPreco", "15000")
                        .header("Authorization", "Bearer " + tokenUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("deve retornar 404 ao detalhar veículo inexistente")
    void deveRetornar404ParaVeiculoInexistente() throws Exception {
        mockMvc.perform(get("/veiculos/{id}", "00000000-0000-0000-0000-000000000000")
                        .header("Authorization", "Bearer " + tokenUser))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Veículo não encontrado"));
    }

    @Test
    @DisplayName("deve rejeitar acesso sem token com 401")
    void deveRejeitarSemToken() throws Exception {
        mockMvc.perform(get("/veiculos"))
                .andExpect(status().isUnauthorized());
    }
}

