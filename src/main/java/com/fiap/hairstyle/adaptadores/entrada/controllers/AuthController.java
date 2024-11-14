package com.fiap.hairstyle.adaptadores.entrada;

import com.fiap.hairstyle.adaptadores.entrada.requests.LoginRequest;
import com.fiap.hairstyle.dominio.servico.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pela autenticação de usuários.
 * Gera um token JWT para usuários autenticados com sucesso.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoint para autenticação de usuários")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    /**
     * Endpoint de login do sistema.
     * Retorna um token JWT para requisições autenticadas.
     *
     * @param loginRequest dados de login (username e senha)
     * @return ResponseEntity com token JWT ou mensagem de erro
     */
    @Operation(summary = "Realizar login e obter token JWT")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Autentica as credenciais do usuário
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            // Gera o token JWT após autenticação bem-sucedida
            String token = jwtService.generateToken(loginRequest.getUsername());
            return ResponseEntity.ok("Bearer " + token);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Falha na autenticação: credenciais inválidas.");
        }
    }
}
