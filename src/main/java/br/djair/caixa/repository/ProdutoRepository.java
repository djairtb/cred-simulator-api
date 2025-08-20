package br.djair.caixa.repository;

import br.djair.caixa.model.Telemetria;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProdutoRepository implements PanacheRepository<Telemetria> {
}