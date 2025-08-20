package br.djair.caixa.dto.telemetria;

import lombok.Data;

@Data
public class TelemetriaDTO {
    private String nomeApi;
    private Integer qtdeRequisicoes;
    private long tempoMedio;
    private long tempoMinimo;
    private long tempoMaximo;
    private double percentualSucesso;
}
