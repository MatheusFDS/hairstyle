    package com.fiap.hairstyle.adaptadores.entrada;
    
    import com.fiap.hairstyle.dominio.entidades.Estabelecimento;
    import com.fiap.hairstyle.dominio.repositorios.EstabelecimentoRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    
    import java.util.List;
    import java.util.Optional;
    import java.util.UUID;
    
    @RestController
    @RequestMapping("/api/estabelecimentos")
    public class EstabelecimentoController {
    
        @Autowired
        private EstabelecimentoRepository estabelecimentoRepository;
    
        @GetMapping
        public List<Estabelecimento> listarTodos() {
            return estabelecimentoRepository.findAll();
        }
    
        // Alteração para UUID no PathVariable
        @GetMapping("/{id}")
        public ResponseEntity<Estabelecimento> buscarPorId(@PathVariable UUID id) {
            Optional<Estabelecimento> estabelecimento = estabelecimentoRepository.findById(id);
            return estabelecimento.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        }
    
        @PostMapping
        public Estabelecimento criar(@RequestBody Estabelecimento estabelecimento) {
            return estabelecimentoRepository.save(estabelecimento);
        }
    
        @PutMapping("/{id}")
        public ResponseEntity<Estabelecimento> atualizar(@PathVariable UUID id, @RequestBody Estabelecimento estabelecimentoAtualizado) {
            return estabelecimentoRepository.findById(id).map(estabelecimento -> {
                estabelecimento.setNome(estabelecimentoAtualizado.getNome());
                estabelecimento.setEndereco(estabelecimentoAtualizado.getEndereco());
                estabelecimento.setServicos(estabelecimentoAtualizado.getServicos()); // Atualiza serviços
                estabelecimento.setProfissionais(estabelecimentoAtualizado.getProfissionais()); // Atualiza profissionais
                estabelecimento.setHorariosFuncionamento(estabelecimentoAtualizado.getHorariosFuncionamento());
                estabelecimento.setFotos(estabelecimentoAtualizado.getFotos());
                return ResponseEntity.ok(estabelecimentoRepository.save(estabelecimento));
            }).orElseGet(() -> ResponseEntity.notFound().build());
        }
    
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deletar(@PathVariable UUID id) {
            if (estabelecimentoRepository.existsById(id)) {
                estabelecimentoRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    
        // Novos endpoints de busca e filtragem:
    
        // Busca por nome do estabelecimento
        @GetMapping("/busca")
        public ResponseEntity<List<Estabelecimento>> buscarPorNome(@RequestParam("nome") String nome) {
            List<Estabelecimento> estabelecimentos = estabelecimentoRepository.findByNomeContainingIgnoreCase(nome);
            return ResponseEntity.ok(estabelecimentos);
        }
    
        // Busca por localização
        @GetMapping("/localizacao")
        public ResponseEntity<List<Estabelecimento>> buscarPorEndereco(@RequestParam("localizacao") String localizacao) {
            List<Estabelecimento> estabelecimentos = estabelecimentoRepository.findByEnderecoContainingIgnoreCase(localizacao);
            return ResponseEntity.ok(estabelecimentos);
        }
    
        // Busca por serviço oferecido
        @GetMapping("/servico")
        public ResponseEntity<List<Estabelecimento>> buscarPorServico(@RequestParam("servico") String servico) {
            List<Estabelecimento> estabelecimentos = estabelecimentoRepository.findByServico(servico);
            return ResponseEntity.ok(estabelecimentos);
        }
    
        // Busca por avaliação mínima
        @GetMapping("/avaliacao")
        public ResponseEntity<List<Estabelecimento>> buscarPorAvaliacaoMinima(@RequestParam("notaMinima") double notaMinima) {
            List<Estabelecimento> estabelecimentos = estabelecimentoRepository.findByAvaliacaoMinima(notaMinima);
            return ResponseEntity.ok(estabelecimentos);
        }
    
        // Busca por faixa de preço
        @GetMapping("/faixa-preco")
        public ResponseEntity<List<Estabelecimento>> buscarPorFaixaDePreco(@RequestParam("precoMin") double precoMin, @RequestParam("precoMax") double precoMax) {
            List<Estabelecimento> estabelecimentos = estabelecimentoRepository.findByFaixaDePreco(precoMin, precoMax);
            return ResponseEntity.ok(estabelecimentos);
        }
    
        // Busca por disponibilidade de horário
        @GetMapping("/disponibilidade")
        public ResponseEntity<List<Estabelecimento>> buscarPorDisponibilidade(
                @RequestParam("diaSemana") String diaSemana,
                @RequestParam("hora") String hora) {
            List<Estabelecimento> estabelecimentos = estabelecimentoRepository.findByDisponibilidade(diaSemana, hora);
            return ResponseEntity.ok(estabelecimentos);
        }
    }
