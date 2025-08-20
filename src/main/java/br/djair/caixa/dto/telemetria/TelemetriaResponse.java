package br.djair.caixa.dto.telemetria;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TelemetriaResponse {
    private LocalDate dataReferencia;
    private List<TelemetriaDTO> listaEndpoints;
}
