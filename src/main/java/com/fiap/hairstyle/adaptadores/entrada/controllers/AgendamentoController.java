package com.fiap.hairstyle.adaptadores.entrada.controllers;

import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.dominio.entidades.HorarioDisponivel;
import com.fiap.hairstyle.dominio.entidades.Servico;
import com.fiap.hairstyle.adaptadores.saida.repositorios.AgendamentoRepository;
import com.fiap.hairstyle.adaptadores.saida.repositorios.HorarioDisponivelRepository;
import com.fiap.hairstyle.adaptadores.saida.repositorios.ServicoRepository;
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
    private ServicoRepository servicoRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    @Autowired
    private GoogleCalendarService googleCalendarService;

    @Operation(summary = "Listar todos os agendamentos", description = "Retorna uma lista de todos os agendamentos.")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Agendamento>> listarTodos() {
        logger.info("Listando todos os agendamentos.");
        return ResponseEntity.ok(agendamentoRepository.findAll());
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
        return agendamento.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Criar novo agendamento", description = "Cria um novo agendamento, incluindo verificação de disponibilidade.")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Agendamento> criar(@RequestBody Agendamento agendamento) {
        logger.info("Tentando criar agendamento para o cliente {} no horário {}",
                agendamento.getCliente().getId(), agendamento.getDataHora());

        try {
            LocalDateTime dataHora = agendamento.getDataHora();
            UUID profissionalId = agendamento.getProfissional().getId();
            DayOfWeek diaSemana = dataHora.getDayOfWeek();
            LocalTime hora = dataHora.toLocalTime();

            Optional<Servico> servicoOpt = servicoRepository.findById(agendamento.getServico().getId());
            if (servicoOpt.isEmpty()) {
                logger.warn("Serviço não encontrado.");
                return ResponseEntity.badRequest().build();
            }

            Servico servico = servicoOpt.get();

            if (servico.getDuracao() == null || servico.getDuracao() <= 0) {
                logger.warn("Duração do serviço inválida.");
                return ResponseEntity.badRequest().build();
            }

            List<HorarioDisponivel> horariosDisponiveis = horarioDisponivelRepository.findByProfissionalId(profissionalId);

            if (!horariosDisponiveis.isEmpty() && horariosDisponiveis.stream().noneMatch(h ->
                    h.getDiaSemana().equals(diaSemana) &&
                            !hora.isBefore(h.getHoraInicio()) && !hora.isAfter(h.getHoraFim()))) {
                logger.warn("Horário indisponível para o profissional.");
                return ResponseEntity.badRequest().build();
            }

            if (!agendamentoRepository.findByProfissionalAndDataHora(profissionalId, dataHora).isEmpty()) {
                logger.warn("Profissional já possui um agendamento nesse horário.");
                return ResponseEntity.badRequest().build();
            }

            if (!agendamentoRepository.findByClienteAndDataHora(agendamento.getCliente().getId(), dataHora).isEmpty()) {
                logger.warn("Cliente já possui um agendamento nesse horário.");
                return ResponseEntity.badRequest().build();
            }

            agendamento.setServico(servico);
            Agendamento novoAgendamento = agendamentoRepository.save(agendamento);

            if (googleCalendarService.isEnabled()) {
                try {
                    String eventId = googleCalendarService.criarEvento(
                            "Agendamento - " + servico.getNome(),
                            "Cliente: " + agendamento.getCliente().getNome(),
                            agendamento.getDataHora(),
                            agendamento.getDataHora().plusMinutes(servico.getDuracao())
                    );
                    novoAgendamento.setGoogleCalendarEventId(eventId);
                    novoAgendamento = agendamentoRepository.save(novoAgendamento);
                } catch (Exception e) {
                    logger.warn("Erro ao sincronizar com o Google Calendar. Ignorando integração.", e);
                }
            }

            notificacaoService.enviarConfirmacao(novoAgendamento);
            return ResponseEntity.status(201).body(novoAgendamento);

        } catch (Exception e) {
            logger.error("Erro ao criar agendamento.", e);
            return ResponseEntity.status(500).build();
        }
    }

    @PatchMapping("/{id}/nao-comparecimento")
    public ResponseEntity<?> marcarNaoComparecimento(@PathVariable UUID id) {
        Optional<Agendamento> agendamentoOpt = agendamentoRepository.findById(id);
        if (agendamentoOpt.isPresent()) {
            Agendamento agendamento = agendamentoOpt.get();
            agendamento.setNaoComparecimento(true);
            agendamentoRepository.save(agendamento);
            notificacaoService.enviarNaoComparecimento(agendamento);
            return ResponseEntity.ok("Agendamento marcado como não comparecido.");
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/reagendar")
    public ResponseEntity<?> reagendarAgendamento(@PathVariable UUID id, @RequestBody LocalDateTime novaDataHora) {
        Optional<Agendamento> agendamentoOpt = agendamentoRepository.findById(id);
        if (agendamentoOpt.isPresent()) {
            Agendamento agendamento = agendamentoOpt.get();
            UUID profissionalId = agendamento.getProfissional().getId();
            DayOfWeek diaSemana = novaDataHora.getDayOfWeek();
            LocalTime hora = novaDataHora.toLocalTime();

            List<HorarioDisponivel> horariosDisponiveis = horarioDisponivelRepository.findByProfissionalId(profissionalId);
            if (!horariosDisponiveis.isEmpty() && horariosDisponiveis.stream().noneMatch(h ->
                    h.getDiaSemana().equals(diaSemana) &&
                            !hora.isBefore(h.getHoraInicio()) && !hora.isAfter(h.getHoraFim()))) {
                return ResponseEntity.badRequest().body("Horário indisponível para o profissional no dia solicitado.");
            }

            if (!agendamentoRepository.findByProfissionalAndDataHora(profissionalId, novaDataHora).isEmpty()) {
                return ResponseEntity.badRequest().body("Profissional já possui um agendamento nesse horário.");
            }

            try {
                if (googleCalendarService.isEnabled()) {
                    googleCalendarService.atualizarEvento(
                            agendamento.getGoogleCalendarEventId(),
                            "Agendamento Reagendado - " + agendamento.getServico().getNome(),
                            novaDataHora,
                            novaDataHora.plusMinutes(agendamento.getServico().getDuracao())
                    );
                }
            } catch (Exception e) {
                logger.warn("Erro ao sincronizar com o Google Calendar. Ignorando integração.", e);
            }

            agendamento.setDataHora(novaDataHora);
            agendamento.setNaoComparecimento(false);
            agendamentoRepository.save(agendamento);
            notificacaoService.enviarConfirmacao(agendamento);

            return ResponseEntity.ok("Agendamento reagendado para " + novaDataHora);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarAgendamento(@PathVariable UUID id) {
        Optional<Agendamento> agendamentoOpt = agendamentoRepository.findById(id);
        if (agendamentoOpt.isPresent()) {
            Agendamento agendamento = agendamentoOpt.get();

            try {
                if (googleCalendarService.isEnabled()) {
                    googleCalendarService.deletarEvento(agendamento.getGoogleCalendarEventId());
                }
            } catch (Exception e) {
                logger.warn("Erro ao sincronizar com o Google Calendar. Ignorando integração.", e);
            }

            agendamentoRepository.delete(agendamento);
            notificacaoService.enviarCancelamento(agendamento);
            return ResponseEntity.ok("Agendamento cancelado.");
        }
        return ResponseEntity.notFound().build();
    }
}
