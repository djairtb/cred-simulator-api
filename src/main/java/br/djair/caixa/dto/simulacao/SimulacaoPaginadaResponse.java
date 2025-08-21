package br.djair.caixa.dto.simulacao;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SimulacaoPaginadaResponse<SimulacaoResponse> {
    private int pagina;
    private int qtdRegitrosPagina;
    private int totalPaginas;
    private long qtdRegistros;
    private List<SimulacaoResponse> registros;
}
