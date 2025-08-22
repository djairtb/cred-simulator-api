package br.djair.caixa.service.simulacao;

import br.djair.caixa.model.simulacao.Simulacao;
import br.djair.caixa.model.simulacao.SimulacaoParcela;
import br.djair.caixa.model.produto.Produto;
import br.djair.caixa.repository.SimulacaoRepository;
import br.djair.caixa.service.ProdutoService;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SimulacaoService {
    @Inject
    ProdutoService produtoService;
    @Inject
    CalculadoraService calculadoraService;
    @Inject
    SimulacaoRepository simulacaoRepository;

    public Produto filtrarProduto(BigDecimal valorDesejado, Integer prazoMeses) {
        return produtoService.filtrarProduto(valorDesejado, prazoMeses);
    }

    @Transactional
    public void salvarSimulacao(Simulacao simulacao) {
        simulacaoRepository.persist(simulacao);
    }

    public Simulacao simular(BigDecimal valorDesejado, Integer prazoMeses ){
        //filtra produto
        Produto produto = this.filtrarProduto(valorDesejado,prazoMeses);
        if (produto == null) {
            throw new RuntimeException("Nao encontramos produtos nessa faixa de valor e tempo");
        }

        //calcula as duas bagacas
        List<SimulacaoParcela> parcelasSac = calculadoraService.calcularModoSAC(valorDesejado, produto.getTaxaJuros(), prazoMeses);
        List<SimulacaoParcela> parcelasPrice = calculadoraService.calcularModoPrice(valorDesejado, produto.getTaxaJuros(), prazoMeses);

        BigDecimal valorTotalSac = BigDecimal.ZERO;
        for(SimulacaoParcela parcela : parcelasSac) {
            valorTotalSac = valorTotalSac.add(parcela.getValorPrestacao());
        }
        BigDecimal valorTotalPrice = BigDecimal.ZERO;
        for(SimulacaoParcela parcela : parcelasPrice) {
            valorTotalPrice = valorTotalSac.add(parcela.getValorPrestacao());
        }
        //mergea a duas para peristir
        parcelasSac.addAll(parcelasPrice);
        Simulacao simulacao = new Simulacao(produto.getId(),prazoMeses, valorDesejado,parcelasSac);
        simulacao.setValorTotalSac(valorTotalSac);
        simulacao.setValorTotalPrice(valorTotalPrice);
        this.salvarSimulacao(simulacao);

        return simulacao;
    }

    public List<Simulacao> listarTodas() {
        List<Simulacao> todas = simulacaoRepository.listAll();
        return todas;
    }

}
