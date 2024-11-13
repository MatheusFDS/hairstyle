package com.fiap.hairstyle.adaptadores.entrada;

import com.fiap.hairstyle.dominio.entidades.Avaliacao;
import com.fiap.hairstyle.dominio.entidades.Agendamento;
import com.fiap.hairstyle.dominio.entidades.Profissional;
import com.fiap.hairstyle.dominio.entidades.Estabelecimento;
import com.fiap.hairstyle.dominio.repositorios.AvaliacaoRepository;
import com.fiap.hairstyle.dominio.repositorios.AgendamentoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    // Endpoint para avaliar o profissional
    @PostMapping("/profissional/{agendamentoId}")
    public ResponseEntity<?> avaliarProfissional(
            @PathVariable UUID agendamentoId,
            @Valid @RequestBody Avaliacao avaliacao) {

        Optional<Agendamento> agendamentoOpt = agendamentoRepository.findById(agendamentoId);

        if (agendamentoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Agendamento não encontrado.");
        }

        Agendamento agendamento = agendamentoOpt.get();

        // Só permitir avaliação se o agendamento foi concluído ou marcado como não comparecimento
        if (!agendamento.isNaoComparecimento()) {
            return ResponseEntity.badRequest().body("Agendamento precisa estar concluído para avaliação.");
        }

        Profissional profissional = agendamento.getProfissional();
        avaliacao.setProfissional(profissional);
        avaliacao.setCliente(agendamento.getCliente());

        Avaliacao novaAvaliacao = avaliacaoRepository.save(avaliacao);
        return ResponseEntity.ok(novaAvaliacao);
    }

    // Endpoint para avaliar o estabelecimento
    @PostMapping("/estabelecimento/{agendamentoId}")
    public ResponseEntity<?> avaliarEstabelecimento(
            @PathVariable UUID agendamentoId,
            @Valid @RequestBody Avaliacao avaliacao) {

        Optional<Agendamento> agendamentoOpt = agendamentoRepository.findById(agendamentoId);

        if (agendamentoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Agendamento não encontrado.");
        }

        Agendamento agendamento = agendamentoOpt.get();

        if (!agendamento.isNaoComparecimento()) {
            return ResponseEntity.badRequest().body("Agendamento precisa estar concluído para avaliação.");
        }

        Estabelecimento estabelecimento = agendamento.getProfissional().getEstabelecimento();
        avaliacao.setEstabelecimento(estabelecimento);
        avaliacao.setCliente(agendamento.getCliente());

        Avaliacao novaAvaliacao = avaliacaoRepository.save(avaliacao);
        return ResponseEntity.ok(novaAvaliacao);
    }

    // Endpoint para listar avaliações de um profissional
    @GetMapping("/profissional/{profissionalId}")
    public ResponseEntity<List<Avaliacao>> listarAvaliacoesProfissional(@PathVariable UUID profissionalId) {
        List<Avaliacao> avaliacoes = avaliacaoRepository.findByProfissionalId(profissionalId);
        return ResponseEntity.ok(avaliacoes);
    }

    // Endpoint para listar avaliações de um estabelecimento
    @GetMapping("/estabelecimento/{estabelecimentoId}")
    public ResponseEntity<List<Avaliacao>> listarAvaliacoesEstabelecimento(@PathVariable UUID estabelecimentoId) {
        List<Avaliacao> avaliacoes = avaliacaoRepository.findByEstabelecimentoId(estabelecimentoId);
        return ResponseEntity.ok(avaliacoes);
    }
}
