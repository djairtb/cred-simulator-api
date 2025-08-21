package br.djair.caixa.dto.simulacao;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SimulacaoRequest {
    private BigDecimal valorDesejado;
    private Integer prazo;
}
