package com.fiap.hairstyle.adaptadores.entrada.controllers;

import com.fiap.hairstyle.dominio.entidades.Cliente;
import com.fiap.hairstyle.aplicacao.casosdeuso.cliente.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Endpoints para gerenciamento de clientes. Requer autenticação com token.")
@SecurityRequirement(name = "Bearer Authentication")
public class ClienteController {

    private final ListarClientesUseCase listarClientesUseCase;
    private final BuscarClientePorIdUseCase buscarClientePorIdUseCase;
    private final CriarClienteUseCase criarClienteUseCase;
    private final AtualizarClienteUseCase atualizarClienteUseCase;
    private final DeletarClienteUseCase deletarClienteUseCase;

    public ClienteController(ListarClientesUseCase listarClientesUseCase,
                             BuscarClientePorIdUseCase buscarClientePorIdUseCase,
                             CriarClienteUseCase criarClienteUseCase,
                             AtualizarClienteUseCase atualizarClienteUseCase,
                             DeletarClienteUseCase deletarClienteUseCase) {
        this.listarClientesUseCase = listarClientesUseCase;
        this.buscarClientePorIdUseCase = buscarClientePorIdUseCase;
        this.criarClienteUseCase = criarClienteUseCase;
        this.atualizarClienteUseCase = atualizarClienteUseCase;
        this.deletarClienteUseCase = deletarClienteUseCase;
    }

    @Operation(summary = "Listar todos os clientes")
    @GetMapping
    public List<Cliente> listarTodos() {
        return listarClientesUseCase.executar();
    }

    @Operation(summary = "Buscar cliente por ID")
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@Parameter(description = "ID do cliente") @PathVariable UUID id) {
        return buscarClientePorIdUseCase.executar(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Criar novo cliente")
    @PostMapping
    public Cliente criar(@RequestBody Cliente cliente) {
        return criarClienteUseCase.executar(cliente);
    }

    @Operation(summary = "Atualizar cliente")
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizar(@PathVariable UUID id, @RequestBody Cliente clienteAtualizado) {
        return atualizarClienteUseCase.executar(id, clienteAtualizado)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Deletar cliente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        if (deletarClienteUseCase.executar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
