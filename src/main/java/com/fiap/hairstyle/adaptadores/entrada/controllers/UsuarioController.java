package com.fiap.hairstyle.adaptadores.entrada.controllers;

import com.fiap.hairstyle.dominio.entidades.Usuario;
import com.fiap.hairstyle.adaptadores.saida.repositorios.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Cadastro", description = "Endpoints para criação e gerenciamento de usuários")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Operation(summary = "Listar todos os usuários",
            description = "Retorna uma lista de todos os usuários cadastrados no sistema. Requer autenticação via token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso.")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Operation(summary = "Buscar um usuário pelo ID",
            description = "Busca um usuário específico pelo seu ID. Requer autenticação via token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPorId(
            @Parameter(description = "ID do usuário a ser buscado", required = true) @PathVariable Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Criar novo usuário",
            description = "Cria um novo usuário no sistema. Esta operação não requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Erro na criação: o username já existe.")
    })
    @PostMapping
    public ResponseEntity<Usuario> criarUsuario(@RequestBody Usuario usuario) {
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(null);
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword())); // Codifica a senha
        Usuario novoUsuario = usuarioRepository.save(usuario);
        return ResponseEntity.ok(novoUsuario);
    }

    @Operation(summary = "Atualizar usuário existente",
            description = "Atualiza os dados de um usuário específico, como nome de usuário, senha e papel (role). Requer autenticação via token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(
            @Parameter(description = "ID do usuário a ser atualizado", required = true) @PathVariable Long id,
            @RequestBody Usuario usuarioAtualizado) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setUsername(usuarioAtualizado.getUsername());
            if (!usuarioAtualizado.getPassword().isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(usuarioAtualizado.getPassword())); // Codifica a senha
            }
            usuario.setRole(usuarioAtualizado.getRole());
            Usuario usuarioAtualizadoBD = usuarioRepository.save(usuario);
            return ResponseEntity.ok(usuarioAtualizadoBD);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Deletar usuário pelo ID",
            description = "Exclui um usuário do sistema com base em seu ID. Requer autenticação via token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(
            @Parameter(description = "ID do usuário a ser excluído", required = true) @PathVariable Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
