package com.fiap.hairstyle.adaptadores.entrada.controllers;

import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.dominio.entidades.HorarioDisponivel;
import com.fiap.hairstyle.adaptadores.saida.repositorios.AgendamentoRepository;
import com.fiap.hairstyle.adaptadores.saida.repositorios.HorarioDisponivelRepository;
import com.fiap.hairstyle.dominio.servico.GoogleCalendarService;
import com.fiap.hairstyle.dominio.servico.NotificacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/agendamentos")
@Tag(name = "Agendamentos", description = "Endpoints para gerenciamento de agendamentos de serviços")
public class AgendamentoController {

    private static final Logger logger = LoggerFactory.getLogger(AgendamentoController.class);

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private HorarioDisponivelRepository horarioDisponivelRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    @Autowired
    private GoogleCalendarService googleCalendarService;

    @Operation(summary = "Listar todos os agendamentos", description = "Retorna uma lista de todos os agendamentos.")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Agendamento> listarTodos() {
        logger.info("Listando todos os agendamentos.");
        return agendamentoRepository.findAll();
    }

    @Operation(summary = "Buscar agendamento por ID", description = "Busca um agendamento específico pelo ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento encontrado"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Agendamento> buscarPorId(
            @Parameter(description = "ID do agendamento", required = true) @PathVariable UUID id) {
        logger.info("Buscando agendamento por ID: {}", id);
        Optional<Agendamento> agendamento = agendamentoRepository.findById(id);
        return agendamento.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Criar novo agendamento", description = "Cria um novo agendamento, incluindo verificação de disponibilidade.")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> criar(@RequestBody Agendamento agendamento) {
        logger.info("Tentando criar agendamento para o cliente {} no horário {}",
                agendamento.getCliente().getId(), agendamento.getDataHora());
        try {
            LocalDateTime dataHora = agendamento.getDataHora();
            UUID profissionalId = agendamento.getProfissional().getId();
            DayOfWeek diaSemana = dataHora.getDayOfWeek();
            LocalTime hora = dataHora.toLocalTime();

            List<HorarioDisponivel> horariosDisponiveis = horarioDisponivelRepository.findByProfissionalId(profissionalId);

            if (!horariosDisponiveis.isEmpty()) {
                List<HorarioDisponivel> horariosDoDia = horariosDisponiveis.stream()
                        .filter(horario -> horario.getDiaSemana().equals(diaSemana))
                        .toList();

                if (horariosDoDia.isEmpty()) {
                    return ResponseEntity.badRequest().body("Profissional não atende no dia solicitado.");
                }

                boolean horarioValido = horariosDoDia.stream()
                        .anyMatch(horario -> !hora.isBefore(horario.getHoraInicio()) && !hora.isAfter(horario.getHoraFim()));

                if (!horarioValido) {
                    return ResponseEntity.badRequest().body("Horário indisponível para o profissional no dia solicitado.");
                }
            }

            List<Agendamento> agendamentosProfissional = agendamentoRepository.findByProfissionalAndDataHora(profissionalId, dataHora);
            if (!agendamentosProfissional.isEmpty()) {
                return ResponseEntity.badRequest().body("Profissional já possui um agendamento nesse horário.");
            }

            List<Agendamento> agendamentosCliente = agendamentoRepository.findByClienteAndDataHora(agendamento.getCliente().getId(), dataHora);
            if (!agendamentosCliente.isEmpty()) {
                return ResponseEntity.badRequest().body("Cliente já possui um agendamento nesse horário.");
            }

            // Salva o agendamento
            Agendamento novoAgendamento = agendamentoRepository.save(agendamento);

            // Sincroniza com Google Calendar
            try {
                String eventId = googleCalendarService.criarEvento(
                        "Agendamento - " + agendamento.getServico().getNome(),
                        "Cliente: " + agendamento.getCliente().getNome(),
                        agendamento.getDataHora(),
                        agendamento.getDataHora().plusMinutes(agendamento.getServico().getDuracao())
                );
                novoAgendamento.setGoogleCalendarEventId(eventId); // Salva o ID do evento
                agendamentoRepository.save(novoAgendamento);
            } catch (IOException e) {
                logger.error("Erro ao sincronizar com Google Calendar.", e);
                return ResponseEntity.status(500).body("Erro ao sincronizar com o Google Calendar.");
            }

            // Envia notificação
            notificacaoService.enviarConfirmacao(novoAgendamento);

            return ResponseEntity.ok(novoAgendamento);

        } catch (Exception e) {
            logger.error("Erro ao criar agendamento.", e);
            return ResponseEntity.status(500).body("Erro interno ao criar agendamento.");
        }
    }
}
