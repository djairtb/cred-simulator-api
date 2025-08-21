package br.djair.caixa.resource;

import br.djair.caixa.dto.telemetria.TelemetriaDTO;
import br.djair.caixa.dto.telemetria.TelemetriaResponse;
import br.djair.caixa.interceptor.TelemetriaNotation;
import br.djair.caixa.model.simulacao.Telemetria;
import br.djair.caixa.service.TelemetriaService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/telemetria")
@TelemetriaNotation
public class TelemetriaResource {
    @Inject
    TelemetriaService telemetriaService;

    @GET
    @Path("/{dataReferencia}")
    public TelemetriaResponse listByDate(@PathParam("dataReferencia") String dataReferenciaStr){
        LocalDate hoje = LocalDate.now();
        try {
            Thread.sleep(675);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dataReferencia = LocalDate.parse(dataReferenciaStr, formatter);


        TelemetriaResponse resposta = new TelemetriaResponse();
        resposta.setDataReferencia(dataReferencia);

        List<Telemetria> filtradas;
        if(dataReferencia.isEqual(hoje)){
            filtradas = telemetriaService.listarTelemetriaEmMemoria();
        } else {
            filtradas = telemetriaService.listarPorData(dataReferencia);
        }


        List<TelemetriaDTO> listResposta = filtradas
                .stream()
                .map(tel -> {
                    TelemetriaDTO dto = new TelemetriaDTO();
                    dto.setNomeApi(tel.getNomeEndpoint());
                    dto.setQtdeRequisicoes(tel.getTotalChamadas());
                    dto.setTempoMinimo(tel.getMinimoMilissegundos());
                    dto.setTempoMaximo(tel.getMaximoMilissegundos());
                    dto.setTempoMedio(tel.getMediaMilissegundos());
                    Double taxaSucesso = (tel.getSucessoChamadas() / tel.getTotalChamadas()) * 1.0;
                    dto.setPercentualSucesso(taxaSucesso);
                    return dto;
                })
                .collect(Collectors.toList());
        resposta.setListaEndpoints(listResposta);

        return resposta;
    }

    @GET
    @Path("/consolidada")
    public List<TelemetriaDTO> listAll(){
        List<Telemetria> consolidadas = telemetriaService.consolidarTelemetrias();

        List<TelemetriaDTO> listResposta = consolidadas
                .stream()
                .map(tel -> {
                    TelemetriaDTO dto = new TelemetriaDTO();
                    dto.setNomeApi(tel.getNomeEndpoint());
                    dto.setQtdeRequisicoes(tel.getTotalChamadas());
                    dto.setTempoMinimo(tel.getMinimoMilissegundos());
                    dto.setTempoMaximo(tel.getMaximoMilissegundos());
                    dto.setTempoMedio(tel.getMediaMilissegundos());
                    Double taxaSucesso = (tel.getSucessoChamadas() * 100.0) / tel.getTotalChamadas();
                    dto.setPercentualSucesso(taxaSucesso);
                    return dto;
                })
                .collect(Collectors.toList());
        return listResposta;
    }


}
