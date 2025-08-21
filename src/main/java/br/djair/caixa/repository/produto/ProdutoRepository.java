package br.djair.caixa.repository.produto;

import br.djair.caixa.model.produto.Produto;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.util.Optional;

@ApplicationScoped
public class ProdutoRepository implements PanacheRepository<Produto> {
    @PersistenceContext
    EntityManager em;
    public Optional<Produto> buscarProdutoFiltrado(BigDecimal valorDesejado, int prazo) {
        return em.createNativeQuery(
                        "SELECT * FROM PRODUTO ")
                .getResultStream()
                .findFirst();
    }
}