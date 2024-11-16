package com.fiap.hairstyle.adaptadores.entrada.controllers;

import com.fiap.hairstyle.dominio.entidades.Estabelecimento;
import com.fiap.hairstyle.adaptadores.saida.repositorios.EstabelecimentoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/estabelecimentos")
@Tag(name = "Estabelecimentos", description = "Endpoints para gerenciamento de estabelecimentos")
@SecurityRequirement(name = "Bearer Authentication")
public class EstabelecimentoController {

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    @Operation(summary = "Listar todos os estabelecimentos",
            description = "Retorna uma lista de todos os estabelecimentos cadastrados. É necessário autenticação via token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso.")
    })
    @GetMapping
    public List<Estabelecimento> listarTodos() {
        return estabelecimentoRepository.findAll();
    }

    @Operation(summary = "Buscar estabelecimento por ID",
            description = "Busca os detalhes de um estabelecimento específico pelo seu ID. É necessário autenticação via token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estabelecimento encontrado."),
            @ApiResponse(responseCode = "404", description = "Estabelecimento não encontrado.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Estabelecimento> buscarPorId(
            @Parameter(description = "ID do estabelecimento a ser buscado", required = true) @PathVariable UUID id) {
        Optional<Estabelecimento> estabelecimento = estabelecimentoRepository.findById(id);
        return estabelecimento.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Criar novo estabelecimento",
            description = "Cadastra um novo estabelecimento no sistema. Relacionamentos como profissionais, serviços e avaliações serão ignorados. É necessário autenticação via token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estabelecimento criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro ao criar o estabelecimento.")
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Estabelecimento> criar(
            @Parameter(description = "Dados do estabelecimento a ser criado", required = true) @RequestBody Estabelecimento estabelecimento) {
        estabelecimento.setProfissionais(List.of());
        estabelecimento.setServicos(List.of());
        estabelecimento.setAvaliacoes(List.of());

        Estabelecimento novoEstabelecimento = estabelecimentoRepository.save(estabelecimento);
        return ResponseEntity.ok(novoEstabelecimento);
    }

    @Operation(summary = "Atualizar um estabelecimento",
            description = "Atualiza os dados de um estabelecimento existente. É necessário autenticação via token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estabelecimento atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Estabelecimento não encontrado.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Estabelecimento> atualizar(
            @Parameter(description = "ID do estabelecimento a ser atualizado", required = true) @PathVariable UUID id,
            @Parameter(description = "Dados atualizados do estabelecimento", required = true) @RequestBody Estabelecimento estabelecimentoAtualizado) {
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

    @Operation(summary = "Deletar um estabelecimento",
            description = "Exclui um estabelecimento do sistema pelo seu ID. É necessário autenticação via token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Estabelecimento deletado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Estabelecimento não encontrado.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do estabelecimento a ser deletado", required = true) @PathVariable UUID id) {
        if (estabelecimentoRepository.existsById(id)) {
            estabelecimentoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Filtrar estabelecimentos",
            description = "Permite buscar estabelecimentos por filtros como nome, endereço, faixa de preço, serviço ou avaliação mínima. É necessário autenticação via token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de estabelecimentos retornada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Parâmetros de filtro inválidos.")
    })
    @GetMapping("/filtros")
    public ResponseEntity<List<Estabelecimento>> filtrarEstabelecimentos(
            @Parameter(description = "Filtrar por nome do estabelecimento") @RequestParam(required = false) String nome,
            @Parameter(description = "Filtrar por endereço do estabelecimento") @RequestParam(required = false) String endereco,
            @Parameter(description = "Faixa mínima de preço dos serviços") @RequestParam(required = false) Double precoMin,
            @Parameter(description = "Faixa máxima de preço dos serviços") @RequestParam(required = false) Double precoMax,
            @Parameter(description = "Filtrar por serviço oferecido") @RequestParam(required = false) String servico,
            @Parameter(description = "Nota mínima de avaliação") @RequestParam(required = false) Double avaliacaoMinima) {
        List<Estabelecimento> estabelecimentos = estabelecimentoRepository.filtrarEstabelecimentos(
                nome, endereco, precoMin, precoMax, servico, avaliacaoMinima);
        return ResponseEntity.ok(estabelecimentos);
    }
}
