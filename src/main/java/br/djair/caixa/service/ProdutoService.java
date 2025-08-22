package br.djair.caixa.service;

import br.djair.caixa.model.produto.Produto;
import br.djair.caixa.repository.produto.ProdutoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.hibernate.annotations.Cache;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class ProdutoService {
    @Inject
    ProdutoRepository produtoRepository;


    public List<Produto> carregarProdutos() {
        return produtoRepository.listAll();
    }
    public Produto filtrarProduto(BigDecimal valorDesejado, int prazo) {
        List<Produto> produtos = carregarProdutos();
        return produtos.stream()
                .filter(p -> p.getValorMinimo().compareTo(valorDesejado) <= 0)
                .filter(p -> p.getValorMaximo() == null || p.getValorMaximo().compareTo(valorDesejado) >= 0)
                .filter(p -> p.getMinimoMeses() <= prazo)
                .filter(p -> p.getMaximoMeses() == null || p.getMaximoMeses() >= prazo)
                .findFirst()
                .orElse(null);
    }

    public Produto getByCodigoProduto(Integer codigoProduto) {
        Produto produto = produtoRepository.findById((long)codigoProduto);
        if (produto == null) {
            throw new RuntimeException("Produto nao encontrado com o codigo: " + codigoProduto.toString());
        }
        return produto;
    }
}
