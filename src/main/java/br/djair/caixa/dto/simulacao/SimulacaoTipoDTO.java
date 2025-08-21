package br.djair.caixa.dto.simulacao;

import br.djair.caixa.model.enums.TipoParcelaEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SimulacaoTipoDTO {
    private String tipo;
    private List<SimulacaoParcelaDTO> parcelas;
}
