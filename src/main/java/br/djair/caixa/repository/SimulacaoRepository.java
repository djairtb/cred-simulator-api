package br.djair.caixa.repository;


import br.djair.caixa.model.simulacao.Simulacao;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SimulacaoRepository implements PanacheRepository<Simulacao> {
}
