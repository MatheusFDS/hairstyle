package com.fiap.hairstyle.performance;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class AgendamentoPerformanceTest {

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
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

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
    public void testCriarAgendamentosSimultaneos() throws Exception {
        int numeroDeAgendamentos = 100; // Número de agendamentos simultâneos
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < numeroDeAgendamentos; i++) {
            int finalI = i;
            executor.submit(() -> {
                try {
                    criarAgendamento(finalI);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        boolean terminado = executor.awaitTermination(1, TimeUnit.MINUTES);

        assertTrue(terminado, "Nem todos os agendamentos foram concluídos no tempo esperado.");
    }

    private void criarAgendamento(int indice) throws Exception {
        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setProfissional(profissional);
        agendamento.setServico(servico);
        agendamento.setDataHora(LocalDateTime.now().plusDays(1).plusMinutes(indice));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        String agendamentoJson = objectMapper.writeValueAsString(agendamento);

        HttpEntity<String> request = new HttpEntity<>(agendamentoJson, headers);
        String url = "http://localhost:8080/api/agendamentos";

        ResponseEntity<Agendamento> response = restTemplate.postForEntity(url, request, Agendamento.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() != null && response.getBody().getId() != null);
    }

    private void loginAndStoreToken(String username, String password) {
        String loginUrl = "http://localhost:8080/api/auth/login";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String loginPayload = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        HttpEntity<String> request = new HttpEntity<>(loginPayload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                token = response.getBody().replace("Bearer ", "");
            } else {
                throw new RuntimeException("Falha no login, verifique as credenciais e o endpoint.");
            }
        } catch (Exception e) {
            criarUsuario(username, password);

            ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                token = response.getBody().replace("Bearer ", "");
            } else {
                throw new RuntimeException("Falha no login mesmo após criar o usuário.");
            }
        }
    }

    private void criarUsuario(String username, String password) {
        String registerUrl = "http://localhost:8080/api/usuarios";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String registerPayload = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\",\"role\":\"ROLE_ADMIN\"}",
                username,
                password
        );
        HttpEntity<String> request = new HttpEntity<>(registerPayload, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(registerUrl, request, String.class);
        if (response.getStatusCode() != HttpStatus.CREATED) {
            throw new RuntimeException("Falha ao criar o usuário.");
        }
    }
}
