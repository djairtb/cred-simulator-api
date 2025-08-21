package br.djair.caixa.dto.simulacao;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SimulacaoResponse {
    private long idSimulacao;
    private long codigoProduto;
    private String descricaoProduto;
    private BigDecimal taxaJuros;

    private List<SimulacaoTipoDTO> resultadoSimulacao;
}
