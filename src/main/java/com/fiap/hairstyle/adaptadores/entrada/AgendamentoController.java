package com.fiap.hairstyle.adaptadores.entrada;

import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.dominio.repositorios.AgendamentoRepository;
import com.fiap.hairstyle.servico.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    @GetMapping
    public List<Agendamento> listarTodos() {
        return agendamentoRepository.findAll();
    }

    // Atualização para UUID no PathVariable
    @GetMapping("/{id}")
    public ResponseEntity<Agendamento> buscarPorId(@PathVariable UUID id) {
        Optional<Agendamento> agendamento = agendamentoRepository.findById(id);
        return agendamento.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Atualização para UUID nos parâmetros
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
        // Verificar disponibilidade do profissional usando UUID
        List<Agendamento> agendamentosProfissional = agendamentoRepository.findByProfissionalAndDataHora(
                agendamento.getProfissional().getId(), agendamento.getDataHora());
        if (!agendamentosProfissional.isEmpty()) {
            return ResponseEntity.badRequest().body("Profissional já possui um agendamento nesse horário.");
        }

        // Verificar disponibilidade do cliente usando UUID
        List<Agendamento> agendamentosCliente = agendamentoRepository.findByClienteAndDataHora(
                agendamento.getCliente().getId(), agendamento.getDataHora());
        if (!agendamentosCliente.isEmpty()) {
            return ResponseEntity.badRequest().body("Cliente já possui um agendamento nesse horário.");
        }

        // Salvar novo agendamento e enviar notificação
        Agendamento novoAgendamento = agendamentoRepository.save(agendamento);
        notificacaoService.enviarConfirmacao(novoAgendamento);

        return ResponseEntity.ok(novoAgendamento);
    }

    // Atualização para UUID no PathVariable
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

    // Atualização para UUID no PathVariable
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        if (agendamentoRepository.existsById(id)) {
            agendamentoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
