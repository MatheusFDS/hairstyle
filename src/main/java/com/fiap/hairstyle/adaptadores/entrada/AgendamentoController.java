package com.fiap.hairstyle.adaptadores.entrada;

import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.dominio.entidades.HorarioDisponivel;
import com.fiap.hairstyle.dominio.repositorios.AgendamentoRepository;
import com.fiap.hairstyle.dominio.repositorios.HorarioDisponivelRepository;
import com.fiap.hairstyle.servico.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
public class AgendamentoController {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private HorarioDisponivelRepository horarioDisponivelRepository;

    @Autowired
    private NotificacaoService notificacaoService;

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

        // Consulta os horários disponíveis para o profissional no dia específico
        List<HorarioDisponivel> horariosDisponiveis = horarioDisponivelRepository.findByProfissionalId(profissionalId);

        // Verifica se o profissional tem algum horário de disponibilidade definido
        if (!horariosDisponiveis.isEmpty()) {
            // Filtra os horários que correspondem ao dia da semana do agendamento
            List<HorarioDisponivel> horariosDoDia = horariosDisponiveis.stream()
                    .filter(horario -> horario.getDiaSemana().equals(diaSemana))
                    .toList();

            // Se o profissional não atende no dia solicitado, retorna erro
            if (horariosDoDia.isEmpty()) {
                return ResponseEntity.badRequest().body("Profissional não atende no dia solicitado.");
            }

            // Verifica se a hora do agendamento está dentro de algum intervalo de horário disponível
            boolean horarioValido = horariosDoDia.stream()
                    .anyMatch(horario ->
                            !hora.isBefore(horario.getHoraInicio()) && !hora.isAfter(horario.getHoraFim())
                    );

            if (!horarioValido) {
                return ResponseEntity.badRequest().body("Horário indisponível para o profissional no dia solicitado.");
            }
        }

        // Se o profissional não tem horários definidos, o agendamento pode ser feito em qualquer horário

        // Verifica se o profissional já tem um agendamento para o mesmo horário
        List<Agendamento> agendamentosProfissional = agendamentoRepository.findByProfissionalAndDataHora(profissionalId, dataHora);
        if (!agendamentosProfissional.isEmpty()) {
            return ResponseEntity.badRequest().body("Profissional já possui um agendamento nesse horário.");
        }

        // Verifica se o cliente já tem um agendamento para o mesmo horário
        List<Agendamento> agendamentosCliente = agendamentoRepository.findByClienteAndDataHora(agendamento.getCliente().getId(), dataHora);
        if (!agendamentosCliente.isEmpty()) {
            return ResponseEntity.badRequest().body("Cliente já possui um agendamento nesse horário.");
        }

        // Salva o agendamento
        Agendamento novoAgendamento = agendamentoRepository.save(agendamento);

        // Recarrega o agendamento completo com todos os relacionamentos
        novoAgendamento = agendamentoRepository.findById(novoAgendamento.getId()).orElse(novoAgendamento);

        // Envia a notificação com os dados completos
        notificacaoService.enviarConfirmacao(novoAgendamento);

        return ResponseEntity.ok(novoAgendamento);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Agendamento> atualizar(@PathVariable UUID id, @RequestBody Agendamento agendamentoAtualizado) {
        return agendamentoRepository.findById(id).map(agendamento -> {
            agendamento.setCliente(agendamentoAtualizado.getCliente());
            agendamento.setServico(agendamentoAtualizado.getServico());
            agendamento.setProfissional(agendamentoAtualizado.getProfissional());
            agendamento.setDataHora(agendamentoAtualizado.getDataHora());
            return ResponseEntity.ok(agendamentoRepository.save(agendamento));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        Optional<Agendamento> agendamentoOptional = agendamentoRepository.findById(id);

        if (agendamentoOptional.isPresent()) {
            Agendamento agendamento = agendamentoOptional.get();

            notificacaoService.enviarCancelamento(agendamento);

            agendamentoRepository.delete(agendamento);

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
