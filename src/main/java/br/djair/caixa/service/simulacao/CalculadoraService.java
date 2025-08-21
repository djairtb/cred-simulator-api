package br.djair.caixa.service.simulacao;

import br.djair.caixa.model.simulacao.SimulacaoParcela;
import br.djair.caixa.model.enums.TipoParcelaEnum;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CalculadoraService {

    //isso aqui define a precisao dos caculos com BigDecimal e aredonda para cima xD
    private static final MathContext precisao = new MathContext(12, RoundingMode.HALF_EVEN);

    public List<SimulacaoParcela> calcularModoSAC(BigDecimal valor, BigDecimal taxaMensal, int prazoMeses) {
        List<SimulacaoParcela> parcelas = new ArrayList<>();

        //taxa fixa de amoritzacao devido ao SAC
        BigDecimal valorAmortizacao = valor.divide(BigDecimal.valueOf(prazoMeses), precisao);

        //calcular cada mes separado para definir os juros de cada
        for (int i = 1; i <= prazoMeses; i++) {

            //sempre tem q tirar uma quantidade de parcelas fixa de amortizacao c o tempo (simula o abate)
            BigDecimal saldoDevedor = valor.subtract(valorAmortizacao.multiply(BigDecimal.valueOf(i - 1), precisao));

            //calcula o juros dessa prestacao
            BigDecimal valorJuros = saldoDevedor.multiply(taxaMensal, precisao);
            //parcela = amortizacao + juros
            BigDecimal valorPrestacao = valorAmortizacao.add(valorJuros, precisao);

            parcelas.add(new SimulacaoParcela(i, TipoParcelaEnum.SAC, valorAmortizacao, valorJuros, valorPrestacao));
        }
        return parcelas;
    }

    public List<SimulacaoParcela> calcularModoPrice(BigDecimal valor, BigDecimal taxaMensal, int prazoMeses) {
        //cola = saldoDevedor * taxaMensal / (1 - (1 + taxaMensal)^(-prazo) )

        List<SimulacaoParcela> parcelas = new ArrayList<>();

        //(1 + taxaMensal)
        BigDecimal umMaisTaxaMensal = BigDecimal.ONE.add(taxaMensal, precisao);

        // (1 + taxaMensal)^-prazo
        BigDecimal potencia = BigDecimal.ONE.divide(umMaisTaxaMensal.pow(prazoMeses, precisao), precisao);
        //(1 - (1 + taxaMensal)^(-prazo) )

        BigDecimal potenciaMenosUm = BigDecimal.ONE.subtract(potencia, precisao);

        //saldoDevedor * taxaMensal / (1 - (1 + taxaMensal)^(-prazo) )
        BigDecimal valorParcela = valor.multiply(taxaMensal, precisao).divide(potenciaMenosUm, precisao);

        BigDecimal saldoDevedor = valor;
        for (int i = 1; i <= prazoMeses; i++) {
            //calcula juros e valor amortizaod por mes
            BigDecimal juros = saldoDevedor.multiply(taxaMensal, precisao);
            BigDecimal amortizacao = valorParcela.subtract(juros, precisao);
            saldoDevedor = saldoDevedor.subtract(amortizacao, precisao);

            parcelas.add(new SimulacaoParcela(i, TipoParcelaEnum.PRICE,amortizacao,juros,valorParcela));
        }
        return parcelas;
    }
}
