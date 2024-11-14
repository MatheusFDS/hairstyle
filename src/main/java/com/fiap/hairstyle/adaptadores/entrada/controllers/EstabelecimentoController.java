package com.fiap.hairstyle.adaptadores.entrada;

import com.fiap.hairstyle.dominio.entidades.Estabelecimento;
import com.fiap.hairstyle.dominio.repositorios.EstabelecimentoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/estabelecimentos")
@Tag(name = "Estabelecimentos", description = "Endpoints para gerenciamento e busca de estabelecimentos")
public class EstabelecimentoController {

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    @Operation(summary = "Listar todos os estabelecimentos", description = "Retorna uma lista de todos os estabelecimentos cadastrados.")
    @GetMapping
    public List<Estabelecimento> listarTodos() {
        return estabelecimentoRepository.findAll();
    }

    @Operation(summary = "Buscar estabelecimento por ID", description = "Busca um estabelecimento específico pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estabelecimento encontrado"),
            @ApiResponse(responseCode = "404", description = "Estabelecimento não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Estabelecimento> buscarPorId(
            @Parameter(description = "ID do estabelecimento", required = true) @PathVariable UUID id) {
        Optional<Estabelecimento> estabelecimento = estabelecimentoRepository.findById(id);
        return estabelecimento.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Criar novo estabelecimento", description = "Adiciona um novo estabelecimento ao sistema.")
    @ApiResponse(responseCode = "201", description = "Estabelecimento criado com sucesso")
    @PostMapping
    public Estabelecimento criar(@RequestBody Estabelecimento estabelecimento) {
        return estabelecimentoRepository.save(estabelecimento);
    }

    @Operation(summary = "Atualizar estabelecimento", description = "Atualiza as informações de um estabelecimento específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estabelecimento atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Estabelecimento não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Estabelecimento> atualizar(
            @Parameter(description = "ID do estabelecimento", required = true) @PathVariable UUID id,
            @RequestBody Estabelecimento estabelecimentoAtualizado) {
        return estabelecimentoRepository.findById(id).map(estabelecimento -> {
            estabelecimento.setNome(estabelecimentoAtualizado.getNome());
            estabelecimento.setEndereco(estabelecimentoAtualizado.getEndereco());
            estabelecimento.setServicos(estabelecimentoAtualizado.getServicos());
            estabelecimento.setProfissionais(estabelecimentoAtualizado.getProfissionais());
            estabelecimento.setHorariosFuncionamento(estabelecimentoAtualizado.getHorariosFuncionamento());
            estabelecimento.setFotos(estabelecimentoAtualizado.getFotos());
            return ResponseEntity.ok(estabelecimentoRepository.save(estabelecimento));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Deletar estabelecimento", description = "Exclui um estabelecimento específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Estabelecimento excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Estabelecimento não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do estabelecimento", required = true) @PathVariable UUID id) {
        if (estabelecimentoRepository.existsById(id)) {
            estabelecimentoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoints de busca e filtragem:

    @Operation(summary = "Buscar estabelecimentos por nome", description = "Busca estabelecimentos que contenham o nome fornecido.")
    @GetMapping("/busca")
    public ResponseEntity<List<Estabelecimento>> buscarPorNome(
            @Parameter(description = "Nome ou parte do nome do estabelecimento") @RequestParam("nome") String nome) {
        List<Estabelecimento> estabelecimentos = estabelecimentoRepository.findByNomeContainingIgnoreCase(nome);
        return ResponseEntity.ok(estabelecimentos);
    }

    @Operation(summary = "Buscar estabelecimentos por localização", description = "Busca estabelecimentos que estejam em uma localização específica.")
    @GetMapping("/localizacao")
    public ResponseEntity<List<Estabelecimento>> buscarPorEndereco(
            @Parameter(description = "Localização (endereço) do estabelecimento") @RequestParam("localizacao") String localizacao) {
        List<Estabelecimento> estabelecimentos = estabelecimentoRepository.findByEnderecoContainingIgnoreCase(localizacao);
        return ResponseEntity.ok(estabelecimentos);
    }

    @Operation(summary = "Buscar estabelecimentos por serviço", description = "Busca estabelecimentos que oferecem um serviço específico.")
    @GetMapping("/servico")
    public ResponseEntity<List<Estabelecimento>> buscarPorServico(
            @Parameter(description = "Nome do serviço oferecido") @RequestParam("servico") String servico) {
        List<Estabelecimento> estabelecimentos = estabelecimentoRepository.findByServico(servico);
        return ResponseEntity.ok(estabelecimentos);
    }

    @Operation(summary = "Buscar estabelecimentos por avaliação mínima", description = "Busca estabelecimentos com média de avaliação maior ou igual à nota mínima fornecida.")
    @GetMapping("/avaliacao")
    public ResponseEntity<List<Estabelecimento>> buscarPorAvaliacaoMinima(
            @Parameter(description = "Nota mínima de avaliação") @RequestParam("notaMinima") double notaMinima) {
        List<Estabelecimento> estabelecimentos = estabelecimentoRepository.findByAvaliacaoMinima(notaMinima);
        return ResponseEntity.ok(estabelecimentos);
    }

    @Operation(summary = "Buscar estabelecimentos por faixa de preço", description = "Busca estabelecimentos que oferecem serviços dentro de uma faixa de preço.")
    @GetMapping("/faixa-preco")
    public ResponseEntity<List<Estabelecimento>> buscarPorFaixaDePreco(
            @Parameter(description = "Preço mínimo") @RequestParam("precoMin") double precoMin,
            @Parameter(description = "Preço máximo") @RequestParam("precoMax") double precoMax) {
        List<Estabelecimento> estabelecimentos = estabelecimentoRepository.findByFaixaDePreco(precoMin, precoMax);
        return ResponseEntity.ok(estabelecimentos);
    }

    @Operation(summary = "Buscar estabelecimentos por disponibilidade", description = "Busca estabelecimentos que possuem horários disponíveis em um dia e horário específicos.")
    @GetMapping("/disponibilidade")
    public ResponseEntity<List<Estabelecimento>> buscarPorDisponibilidade(
            @Parameter(description = "Dia da semana") @RequestParam("diaSemana") String diaSemana,
            @Parameter(description = "Horário disponível no formato HH:mm") @RequestParam("hora") String hora) {
        List<Estabelecimento> estabelecimentos = estabelecimentoRepository.findByDisponibilidade(diaSemana, hora);
        return ResponseEntity.ok(estabelecimentos);
    }
}
