package com.fiap.hairstyle.adaptadores.entrada.controllers;

import com.fiap.hairstyle.dominio.entidades.Estabelecimento;
import com.fiap.hairstyle.dominio.entidades.Servico;
import com.fiap.hairstyle.adaptadores.saida.repositorios.EstabelecimentoRepository;
import com.fiap.hairstyle.adaptadores.saida.repositorios.ServicoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/servicos")
@Tag(name = "Serviços", description = "Endpoints para gerenciamento de serviços oferecidos pelos estabelecimentos")
@SecurityRequirement(name = "Bearer Authentication")
public class ServicoController {

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    @Operation(summary = "Listar todos os serviços",
            description = "Retorna uma lista de todos os serviços disponíveis. Requer autenticação via token JWT.")
    @ApiResponse(responseCode = "200", description = "Lista de serviços retornada com sucesso.")
    @GetMapping(produces = "application/json")
    public List<Servico> listarTodos() {
        return servicoRepository.findAll();
    }

    @Operation(summary = "Buscar serviço por ID",
            description = "Busca um serviço específico pelo ID. Requer autenticação via token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço encontrado."),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado.")
    })
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Servico> buscarPorId(
            @Parameter(description = "ID do serviço", required = true) @PathVariable UUID id) {
        Optional<Servico> servico = servicoRepository.findById(id);
        return servico.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Criar novo serviço",
            description = "Cadastra um novo serviço no sistema. O estabelecimento é obrigatório. Requer autenticação via token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Serviço criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou estabelecimento não encontrado.")
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> criar(
            @Parameter(description = "Dados do serviço a ser criado", required = true) @Valid @RequestBody Servico servico) {
        if (servico.getEstabelecimento() == null || servico.getEstabelecimento().getId() == null) {
            return ResponseEntity.badRequest().body("Estabelecimento é obrigatório.");
        }

        Optional<Estabelecimento> estabelecimentoOpt = estabelecimentoRepository.findById(servico.getEstabelecimento().getId());
        if (!estabelecimentoOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Estabelecimento não encontrado.");
        }

        servico.setEstabelecimento(estabelecimentoOpt.get());
        Servico novoServico = servicoRepository.save(servico);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoServico);
    }

    @Operation(summary = "Atualizar serviço",
            description = "Atualiza as informações de um serviço específico. Requer autenticação via token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou estabelecimento não encontrado."),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado.")
    })
    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> atualizar(
            @Parameter(description = "ID do serviço", required = true) @PathVariable UUID id,
            @Parameter(description = "Dados do serviço a ser atualizado", required = true) @Valid @RequestBody Servico servicoAtualizado) {
        return servicoRepository.findById(id).map(servico -> {
            servico.setNome(servicoAtualizado.getNome());
            servico.setDescricao(servicoAtualizado.getDescricao());
            servico.setPreco(servicoAtualizado.getPreco());
            servico.setDuracao(servicoAtualizado.getDuracao());

            if (servicoAtualizado.getEstabelecimento() != null && servicoAtualizado.getEstabelecimento().getId() != null) {
                Optional<Estabelecimento> estabelecimentoOpt = estabelecimentoRepository.findById(servicoAtualizado.getEstabelecimento().getId());
                if (!estabelecimentoOpt.isPresent()) {
                    return ResponseEntity.badRequest().body("Estabelecimento não encontrado.");
                }
                servico.setEstabelecimento(estabelecimentoOpt.get());
            } else {
                return ResponseEntity.badRequest().body("Estabelecimento é obrigatório.");
            }

            Servico servicoSalvo = servicoRepository.save(servico);
            return ResponseEntity.ok(servicoSalvo);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Deletar serviço",
            description = "Exclui um serviço específico do sistema. Requer autenticação via token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Serviço excluído com sucesso."),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado.")
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
