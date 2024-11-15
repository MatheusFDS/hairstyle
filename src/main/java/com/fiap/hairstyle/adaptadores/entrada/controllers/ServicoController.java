package com.fiap.hairstyle.adaptadores.entrada.controllers;

import com.fiap.hairstyle.dominio.entidades.Servico;
import com.fiap.hairstyle.adaptadores.saida.repositorios.ServicoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/servicos")
@Tag(name = "Serviços", description = "Endpoints para gerenciamento de serviços oferecidos pelos estabelecimentos")
public class ServicoController {

    @Autowired
    private ServicoRepository servicoRepository;

    @Operation(summary = "Listar todos os serviços", description = "Retorna uma lista de todos os serviços disponíveis.")
    @GetMapping
    public List<Servico> listarTodos() {
        return servicoRepository.findAll();
    }

    @Operation(summary = "Buscar serviço por ID", description = "Busca um serviço específico pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço encontrado"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Servico> buscarPorId(
            @Parameter(description = "ID do serviço", required = true) @PathVariable UUID id) {
        Optional<Servico> servico = servicoRepository.findById(id);
        return servico.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Criar novo serviço", description = "Cadastra um novo serviço no sistema.")
    @ApiResponse(responseCode = "201", description = "Serviço criado com sucesso")
    @PostMapping
    public Servico criar(@RequestBody Servico servico) {
        return servicoRepository.save(servico);
    }

    @Operation(summary = "Atualizar serviço", description = "Atualiza as informações de um serviço específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Servico> atualizar(
            @Parameter(description = "ID do serviço", required = true) @PathVariable UUID id,
            @RequestBody Servico servicoAtualizado) {
        return servicoRepository.findById(id).map(servico -> {
            servico.setNome(servicoAtualizado.getNome());
            servico.setDescricao(servicoAtualizado.getDescricao());
            servico.setPreco(servicoAtualizado.getPreco());
            servico.setDuracao(servicoAtualizado.getDuracao());
            servico.setEstabelecimento(servicoAtualizado.getEstabelecimento());
            return ResponseEntity.ok(servicoRepository.save(servico));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Deletar serviço", description = "Exclui um serviço específico do sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Serviço excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do serviço", required = true) @PathVariable UUID id) {
        if (servicoRepository.existsById(id)) {
            servicoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
