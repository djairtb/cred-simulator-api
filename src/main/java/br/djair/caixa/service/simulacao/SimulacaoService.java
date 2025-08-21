package br.djair.caixa.service.simulacao;

import br.djair.caixa.model.simulacao.Simulacao;
import br.djair.caixa.model.simulacao.SimulacaoParcela;
import br.djair.caixa.model.produto.Produto;
import br.djair.caixa.repository.SimulacaoRepository;
import br.djair.caixa.service.ProdutoService;
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

        //calcula as duas bagacas
        List<SimulacaoParcela> parcelasSac = calculadoraService.calcularModoSAC(valorDesejado, produto.getTaxaJuros(), prazoMeses);
        List<SimulacaoParcela> parcelasPrice = calculadoraService.calcularModoPrice(valorDesejado, produto.getTaxaJuros(), prazoMeses);

        //mergea a duas para peristir
        parcelasSac.addAll(parcelasPrice);
        Simulacao simulacao = new Simulacao(produto.getId(),prazoMeses, valorDesejado,parcelasSac);
        this.salvarSimulacao(simulacao);

        return simulacao;
    }

    public List<Simulacao> listarTodas() {
        return simulacaoRepository.listAll();
    }

//    public List<Object[]> listarPorProdutoEDia() {
//        return simulacaoRepository.buscarAgrupadoPorProdutoEDia();
//    }
//
}
