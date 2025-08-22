package br.djair.caixa.dto.simulacao;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimulacaoResponse {
    private long idSimulacao;
    private long codigoProduto;
    private String descricaoProduto;
    private BigDecimal taxaJuros;
    private Integer quantidadeParcelas;
    private BigDecimal valorFinanciado;
    private BigDecimal valorTotalSAC;
    private BigDecimal valorTotalPRICE;

    private List<SimulacaoTipoDTO> resultadoSimulacao;

    public SimulacaoResponse(long idSimulacao,
                             long codigoProduto,
                             String descricaoProduto,
                             BigDecimal taxaJuros,
                             Integer quantidadeParcelas,
                             BigDecimal valorFinanciado,
                             BigDecimal valorTotalSAC,
                             BigDecimal valorTotalPRICE) {
        this.idSimulacao = idSimulacao;
        this.codigoProduto = codigoProduto;
        this.descricaoProduto = descricaoProduto;
        this.taxaJuros = taxaJuros.setScale(4, RoundingMode.HALF_EVEN);
        this.quantidadeParcelas = quantidadeParcelas;
        this.valorFinanciado = roundBigDecimal(valorFinanciado);
        this.valorTotalSAC = roundBigDecimal(valorTotalSAC);
        this.valorTotalPRICE = roundBigDecimal(valorTotalPRICE);
        }

        //poupa-dedos
        private BigDecimal roundBigDecimal(BigDecimal value) {
            if (value == null) return null;
            return value.setScale(2, RoundingMode.HALF_EVEN);
        }

}
