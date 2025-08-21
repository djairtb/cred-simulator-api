package br.djair.caixa.dto.simulacao;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class SimulacaoParcelaDTO {
    private Integer numero;
    private BigDecimal valorJuros;
    private BigDecimal valorAmortizacao;
    private BigDecimal valorPrestacao;

    public SimulacaoParcelaDTO(Integer numero, BigDecimal valorJuros, BigDecimal valorAmortizacao, BigDecimal valorPrestacao) {
        this.numero = numero;
        this.valorJuros = valorJuros.setScale(2, RoundingMode.HALF_EVEN);
        this.valorAmortizacao = valorAmortizacao.setScale(2, RoundingMode.HALF_EVEN);
        this.valorPrestacao = valorPrestacao.setScale(2, RoundingMode.HALF_EVEN);
    }
}
