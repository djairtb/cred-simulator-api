package br.djair.caixa.repository;

import br.djair.caixa.model.simulacao.SimulacaoParcela;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SimulacaoParcelaRepository implements PanacheRepository<SimulacaoParcela> {
}
