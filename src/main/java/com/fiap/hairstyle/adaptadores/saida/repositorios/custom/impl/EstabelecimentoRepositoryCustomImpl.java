package com.fiap.hairstyle.adaptadores.saida.repositorios.custom.impl;

import com.fiap.hairstyle.adaptadores.saida.repositorios.custom.EstabelecimentoRepositoryCustom;
import com.fiap.hairstyle.dominio.entidades.Estabelecimento;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class EstabelecimentoRepositoryCustomImpl implements EstabelecimentoRepositoryCustom {

    private final EntityManager entityManager;

    public EstabelecimentoRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Estabelecimento> filtrarEstabelecimentos(String nome, String endereco, Double precoMin, Double precoMax, String servico, Double avaliacaoMinima) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Estabelecimento> query = cb.createQuery(Estabelecimento.class);
        Root<Estabelecimento> root = query.from(Estabelecimento.class);
        List<Predicate> predicates = new ArrayList<>();

        // Filtro por nome
        if (nome != null && !nome.isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%"));
        }

        // Filtro por endereço
        if (endereco != null && !endereco.isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("endereco")), "%" + endereco.toLowerCase() + "%"));
        }

        // Filtro por faixa de preço
        if (precoMin != null || precoMax != null) {
            Join<?, ?> servicos = root.join("servicos", JoinType.LEFT);
            if (precoMin != null) {
                predicates.add(cb.greaterThanOrEqualTo(servicos.get("preco"), precoMin));
            }
            if (precoMax != null) {
                predicates.add(cb.lessThanOrEqualTo(servicos.get("preco"), precoMax));
            }
        }

        // Filtro por serviço
        if (servico != null && !servico.isEmpty()) {
            Join<?, ?> servicos = root.join("servicos", JoinType.LEFT);
            predicates.add(cb.like(cb.lower(servicos.get("nome")), "%" + servico.toLowerCase() + "%"));
        }

        // Filtro por avaliação mínima
        if (avaliacaoMinima != null) {
            Join<?, ?> avaliacoes = root.join("avaliacoes", JoinType.LEFT);
            Expression<Double> mediaNotas = cb.avg(avaliacoes.get("nota"));
            predicates.add(cb.greaterThanOrEqualTo(mediaNotas, avaliacaoMinima));
        }

        query.select(root).where(predicates.toArray(new Predicate[0]));
        query.distinct(true);

        TypedQuery<Estabelecimento> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }
}
