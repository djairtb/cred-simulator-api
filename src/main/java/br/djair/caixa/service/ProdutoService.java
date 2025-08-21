package br.djair.caixa.service;

import br.djair.caixa.dto.simulacao.SimulacaoRequest;
import br.djair.caixa.model.produto.Produto;
import br.djair.caixa.repository.produto.ProdutoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;

@ApplicationScoped
public class ProdutoService {
    @Inject
    ProdutoRepository produtoRepository;


    public Produto filtrarProduto(BigDecimal valorDesejado,Integer prazo) {
//        Produto prod = produtoRepository.buscarProdutoFiltrado(valorDesejado, prazo).orElse(null);
//        if (prod != null) {
//            throw new RuntimeException("Sem produtos encontrados com essas condicoes!");
//        }

        Produto prod = produtoRepository.listAll().getFirst();

        return prod;
    }

    public Produto getByCodigoProduto(Integer codigoProduto) {
        Produto produto = produtoRepository.findById((long)codigoProduto);
        if (produto == null) {
            throw new RuntimeException("Produto nao encontrado com o codigo: " + codigoProduto.toString());
        }
        return produto;
    }
}
