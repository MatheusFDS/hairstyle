package com.fiap.hairstyle.adaptadores.entrada;

import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.dominio.entidades.HorarioDisponivel;
import com.fiap.hairstyle.dominio.repositorios.AgendamentoRepository;
import com.fiap.hairstyle.dominio.repositorios.HorarioDisponivelRepository;
import com.fiap.hairstyle.servico.GoogleCalendarService;
import com.fiap.hairstyle.servico.NotificacaoService;
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
public class AgendamentoController {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private HorarioDisponivelRepository horarioDisponivelRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    @Autowired
    private GoogleCalendarService googleCalendarService;

    @GetMapping
    public List<Agendamento> listarTodos() {
        return agendamentoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agendamento> buscarPorId(@PathVariable UUID id) {
        Optional<Agendamento> agendamento = agendamentoRepository.findById(id);
        return agendamento.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Agendamento> listarPorCliente(@PathVariable UUID clienteId) {
        return agendamentoRepository.findByClienteId(clienteId);
    }

    @GetMapping("/profissional/{profissionalId}")
    public List<Agendamento> listarPorProfissional(@PathVariable UUID profissionalId) {
        return agendamentoRepository.findByProfissionalId(profissionalId);
    }

    @GetMapping("/periodo")
    public List<Agendamento> listarPorPeriodo(@RequestParam LocalDateTime inicio, @RequestParam LocalDateTime fim) {
        return agendamentoRepository.findByPeriodo(inicio, fim);
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Agendamento agendamento) {
        LocalDateTime dataHora = agendamento.getDataHora();
        UUID profissionalId = agendamento.getProfissional().getId();
        DayOfWeek diaSemana = dataHora.getDayOfWeek();
        LocalTime hora = dataHora.toLocalTime();

        // Verificações de disponibilidade e conflitos
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

            // Verificações de disponibilidade
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

            // Atualiza no Google Calendar
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

            // Atualiza o agendamento
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

            // Remove do Google Calendar
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
