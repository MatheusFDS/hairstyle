package com.fiap.hairstyle.adaptadores.entrada;

import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.dominio.entidades.HorarioDisponivel;
import com.fiap.hairstyle.dominio.repositorios.AgendamentoRepository;
import com.fiap.hairstyle.dominio.repositorios.HorarioDisponivelRepository;
import com.fiap.hairstyle.dominio.servico.GoogleCalendarService;
import com.fiap.hairstyle.dominio.servico.NotificacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private HorarioDisponivelRepository horarioDisponivelRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    @Autowired
    private GoogleCalendarService googleCalendarService;

    @Operation(summary = "Listar todos os agendamentos", description = "Retorna uma lista de todos os agendamentos.")
    @GetMapping
    public List<Agendamento> listarTodos() {
        return agendamentoRepository.findAll();
    }

    @Operation(summary = "Buscar agendamento por ID", description = "Busca um agendamento específico pelo ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento encontrado"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Agendamento> buscarPorId(
            @Parameter(description = "ID do agendamento", required = true) @PathVariable UUID id) {
        Optional<Agendamento> agendamento = agendamentoRepository.findById(id);
        return agendamento.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar agendamentos por cliente", description = "Retorna todos os agendamentos de um cliente específico.")
    @GetMapping("/cliente/{clienteId}")
    public List<Agendamento> listarPorCliente(
            @Parameter(description = "ID do cliente", required = true) @PathVariable UUID clienteId) {
        return agendamentoRepository.findByClienteId(clienteId);
    }

    @Operation(summary = "Listar agendamentos por profissional", description = "Retorna todos os agendamentos de um profissional específico.")
    @GetMapping("/profissional/{profissionalId}")
    public List<Agendamento> listarPorProfissional(
            @Parameter(description = "ID do profissional", required = true) @PathVariable UUID profissionalId) {
        return agendamentoRepository.findByProfissionalId(profissionalId);
    }

    @Operation(summary = "Listar agendamentos por período", description = "Retorna todos os agendamentos em um determinado intervalo de tempo.")
    @GetMapping("/periodo")
    public List<Agendamento> listarPorPeriodo(
            @Parameter(description = "Data e hora de início do período", required = true) @RequestParam LocalDateTime inicio,
            @Parameter(description = "Data e hora de fim do período", required = true) @RequestParam LocalDateTime fim) {
        return agendamentoRepository.findByPeriodo(inicio, fim);
    }

    @Operation(summary = "Criar novo agendamento", description = "Cria um novo agendamento, incluindo verificação de disponibilidade.")
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Agendamento agendamento) {
        // Código de verificação de disponibilidade
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
                    .anyMatch(horario ->
                            !hora.isBefore(horario.getHoraInicio()) && !hora.isAfter(horario.getHoraFim())
                    );

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
            return ResponseEntity.status(500).body("Erro ao sincronizar com o Google Calendar.");
        }

        // Envia notificação
        notificacaoService.enviarConfirmacao(novoAgendamento);

        return ResponseEntity.ok(novoAgendamento);
    }

    @Operation(summary = "Marcar agendamento como não comparecimento", description = "Marca um agendamento específico como não comparecido.")
    @PatchMapping("/{id}/nao-comparecimento")
    public ResponseEntity<?> marcarNaoComparecimento(
            @Parameter(description = "ID do agendamento", required = true) @PathVariable UUID id) {
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

    @Operation(summary = "Reagendar agendamento", description = "Reagenda um agendamento existente para uma nova data e hora.")
    @PutMapping("/{id}/reagendar")
    public ResponseEntity<?> reagendarAgendamento(
            @Parameter(description = "ID do agendamento", required = true) @PathVariable UUID id,
            @RequestBody LocalDateTime novaDataHora) {
        Optional<Agendamento> agendamentoOpt = agendamentoRepository.findById(id);
        if (agendamentoOpt.isPresent()) {
            Agendamento agendamento = agendamentoOpt.get();
            UUID profissionalId = agendamento.getProfissional().getId();
            DayOfWeek diaSemana = novaDataHora.getDayOfWeek();
            LocalTime hora = novaDataHora.toLocalTime();

            List<HorarioDisponivel> horariosDisponiveis = horarioDisponivelRepository.findByProfissionalId(profissionalId);
            if (!horariosDisponiveis.isEmpty()) {
                List<HorarioDisponivel> horariosDoDia = horariosDisponiveis.stream()
                        .filter(horario -> horario.getDiaSemana().equals(diaSemana))
                        .toList();

                if (horariosDoDia.isEmpty()) {
                    return ResponseEntity.badRequest().body("Profissional não atende no dia solicitado.");
                }

                boolean horarioValido = horariosDoDia.stream()
                        .anyMatch(horario ->
                                !hora.isBefore(horario.getHoraInicio()) && !hora.isAfter(horario.getHoraFim())
                        );

                if (!horarioValido) {
                    return ResponseEntity.badRequest().body("Horário indisponível para o profissional no dia solicitado.");
                }
            }

            List<Agendamento> agendamentosProfissional = agendamentoRepository.findByProfissionalAndDataHora(profissionalId, novaDataHora);
            if (!agendamentosProfissional.isEmpty()) {
                return ResponseEntity.badRequest().body("Profissional já possui um agendamento nesse horário.");
            }

            try {
                googleCalendarService.atualizarEvento(
                        agendamento.getGoogleCalendarEventId(),
                        "Agendamento Reagendado - " + agendamento.getServico().getNome(),
                        novaDataHora,
                        novaDataHora.plusMinutes(agendamento.getServico().getDuracao())
                );
            } catch (IOException e) {
                return ResponseEntity.status(500).body("Erro ao sincronizar com o Google Calendar.");
            }

            agendamento.setDataHora(novaDataHora);
            agendamento.setNaoComparecimento(false);
            agendamentoRepository.save(agendamento);
            notificacaoService.enviarConfirmacao(agendamento);

            return ResponseEntity.ok("Agendamento reagendado para " + novaDataHora);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Cancelar agendamento", description = "Cancela um agendamento existente.")
    @DeleteMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarAgendamento(
            @Parameter(description = "ID do agendamento", required = true) @PathVariable UUID id) {
        Optional<Agendamento> agendamentoOpt = agendamentoRepository.findById(id);
        if (agendamentoOpt.isPresent()) {
            Agendamento agendamento = agendamentoOpt.get();
            try {
                googleCalendarService.deletarEvento(agendamento.getGoogleCalendarEventId());
            } catch (IOException e) {
                return ResponseEntity.status(500).body("Erro ao sincronizar com o Google Calendar.");
            }

            agendamentoRepository.delete(agendamento);
            notificacaoService.enviarCancelamento(agendamento);
            return ResponseEntity.ok("Agendamento cancelado.");
        }
        return ResponseEntity.notFound().build();
    }
}
