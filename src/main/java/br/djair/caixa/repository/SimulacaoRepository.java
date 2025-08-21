package br.djair.caixa.repository;


import br.djair.caixa.model.simulacao.Simulacao;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;

import java.util.List;

@ApplicationScoped

public class SimulacaoRepository implements PanacheRepository<Simulacao> {
}
