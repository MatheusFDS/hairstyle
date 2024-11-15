package com.fiap.hairstyle.integration;

import com.fiap.hairstyle.dominio.entidades.*;
import com.fiap.hairstyle.adaptadores.saida.repositorios.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class AgendamentoIntegrationTest {

    @Autowired
    private AgendamentoRepository agendamentoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ProfissionalRepository profissionalRepository;
    @Autowired
    private ServicoRepository servicoRepository;
    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    private RestTemplate restTemplate;
    private String token;
    private Cliente cliente;
    private Profissional profissional;
    private Servico servico;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        restTemplate = new RestTemplateBuilder().build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Suporte para LocalDateTime
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Formato ISO para datas

        loginAndStoreToken("matheus", "Ma-241194");

        cliente = new Cliente();
        cliente.setId(UUID.randomUUID());
        cliente.setNome("João");
        cliente.setEmail("joao@example.com");
        cliente.setTelefone("123456789");
        cliente = clienteRepository.save(cliente);

        Estabelecimento estabelecimento = new Estabelecimento();
        estabelecimento.setId(UUID.randomUUID());
        estabelecimento.setNome("Estabelecimento X");
        estabelecimento.setEndereco("Rua A");
        estabelecimento.setHorariosFuncionamento("Seg-Sex: 08:00-18:00");
        estabelecimento = estabelecimentoRepository.save(estabelecimento);

        profissional = new Profissional();
        profissional.setId(UUID.randomUUID());
        profissional.setNome("Maria");
        profissional.setEspecialidade("Cabelereira");
        profissional.setTelefone("123456789");
        profissional.setTarifa(100.0);
        profissional.setEstabelecimento(estabelecimento);
        profissional = profissionalRepository.save(profissional);

        servico = new Servico();
        servico.setId(UUID.randomUUID());
        servico.setNome("Corte de Cabelo");
        servico.setDescricao("Corte profissional");
        servico.setPreco(50.0);
        servico.setDuracao(30);
        servico.setEstabelecimento(estabelecimento);
        servico = servicoRepository.save(servico);
    }

    @Test
    public void testCriarAgendamentoIntegration() throws Exception {
        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setProfissional(profissional);
        agendamento.setServico(servico);
        agendamento.setDataHora(LocalDateTime.now().plusDays(1));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // Tipo de mídia JSON
        headers.setBearerAuth(token); // Autenticação com Bearer token

        // Serializa o objeto Agendamento para JSON
        String agendamentoJson = objectMapper.writeValueAsString(agendamento);

        // Envia a requisição para o endpoint de criação de agendamentos
        HttpEntity<String> request = new HttpEntity<>(agendamentoJson, headers);
        String url = "http://localhost:8080/api/agendamentos";

        ResponseEntity<Agendamento> response = restTemplate.postForEntity(url, request, Agendamento.class);

        // Valida a resposta
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() != null && response.getBody().getId() != null);
    }

    private void loginAndStoreToken(String username, String password) {
        String loginUrl = "http://localhost:8080/api/auth/login";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String loginPayload = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        HttpEntity<String> request = new HttpEntity<>(loginPayload, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, request, String.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            token = response.getBody().replace("Bearer ", "");
        } else {
            throw new RuntimeException("Falha no login, verifique as credenciais e o endpoint.");
        }
    }
}
