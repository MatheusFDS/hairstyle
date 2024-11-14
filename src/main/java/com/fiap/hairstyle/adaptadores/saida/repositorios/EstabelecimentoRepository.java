package com.fiap.hairstyle.dominio.repositorios;

import com.fiap.hairstyle.dominio.entidades.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EstabelecimentoRepository extends JpaRepository<Estabelecimento, UUID> {

    List<Estabelecimento> findByNomeContainingIgnoreCase(String nome);

    // Filtra pelo campo de endereço ao invés de localização
    List<Estabelecimento> findByEnderecoContainingIgnoreCase(String endereco);

    @Query("SELECT e FROM Estabelecimento e JOIN e.servicos s WHERE s.nome = :servico")
    List<Estabelecimento> findByServico(@Param("servico") String servico);

    @Query("SELECT e FROM Estabelecimento e JOIN e.avaliacoes a GROUP BY e HAVING AVG(a.nota) >= :notaMinima")
    List<Estabelecimento> findByAvaliacaoMinima(@Param("notaMinima") double notaMinima);

    // Filtragem por faixa de preço
    @Query("SELECT e FROM Estabelecimento e JOIN e.servicos s WHERE s.preco BETWEEN :precoMin AND :precoMax")
    List<Estabelecimento> findByFaixaDePreco(@Param("precoMin") double precoMin, @Param("precoMax") double precoMax);

    @Query("SELECT e FROM Estabelecimento e JOIN e.profissionais p JOIN p.horariosDisponiveis h WHERE h.diaSemana = :diaSemana AND h.horaInicio <= :hora AND h.horaFim >= :hora")
    List<Estabelecimento> findByDisponibilidade(@Param("diaSemana") String diaSemana, @Param("hora") String hora);
}
