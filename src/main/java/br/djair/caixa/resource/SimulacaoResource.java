package br.djair.caixa.resource;

import br.djair.caixa.dto.PaginadaResponse;
import br.djair.caixa.dto.produto.ProdutoDTO;
import br.djair.caixa.dto.simulacao.*;
import br.djair.caixa.interceptor.TelemetriaNotation;
import br.djair.caixa.model.produto.Produto;
import br.djair.caixa.model.simulacao.Simulacao;
import br.djair.caixa.model.simulacao.SimulacaoParcela;
import br.djair.caixa.model.enums.TipoParcelaEnum;
import br.djair.caixa.producer.EventHubSender;
import br.djair.caixa.service.ProdutoService;
import br.djair.caixa.service.simulacao.SimulacaoService;
import br.djair.caixa.util.PaginationUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/simulacao")
@TelemetriaNotation

public class SimulacaoResource {
    @Inject
    EventHubSender eventHubSender;
    @Inject
    SimulacaoService simulacaoService;
    @Inject
    ProdutoService produtoService;

    @POST
    public Response simular(SimulacaoRequest request) {
        Simulacao novaSimulacao = simulacaoService.simular(request.getValorDesejado(), request.getPrazo());
        SimulacaoResponse dto = geraSimualcaoResponseDTO(novaSimulacao);
        ObjectMapper mapper = new ObjectMapper();

        try {
            String json = mapper.writeValueAsString(dto);
            eventHubSender.sendMessage(json);
        }catch (JsonProcessingException e){
            throw new RuntimeException("Erro ao converter DTO para JSON", e);
        }catch (RuntimeException e){
            throw new WebApplicationException("Erro ao enviar mensagem para o Event Hub", e, Response.Status.INTERNAL_SERVER_ERROR);
        }

        return Response.ok(dto).build();
    }

    @GET
    public Response listarTodas(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("500") int size
    ) {
        List<Simulacao> simulacoes = simulacaoService.listarTodas();
        List<SimulacaoResponse> dtos = new ArrayList<>();
        for(Simulacao simulacao : simulacoes) {
            SimulacaoResponse newDTO = geraSimualcaoResponseDTO(simulacao);
            newDTO.setResultadoSimulacao(null); //tira para n enviar detalhes das parcelas (se quiser bsuca por id)
            dtos.add(newDTO);
        }
        PaginadaResponse<SimulacaoResponse> paginadaResponse = PaginationUtil.paginate(dtos, page, size);

        return Response.ok(paginadaResponse).build();
    }

    @GET
    @Path("{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        if (id == null || id <= 0) {
            throw new WebApplicationException("ID inválido", Response.Status.BAD_REQUEST);
        }
        Simulacao simulacao = simulacaoService.getById(id);
        if (simulacao == null) {
            throw new WebApplicationException("Simulação não encontrada", Response.Status.NOT_FOUND);
        }
        SimulacaoResponse dto = geraSimualcaoResponseDTO(simulacao);
        return Response.ok(dto).build();
    }

    //GERADORES DE DTO
    public SimulacaoResponse geraSimualcaoResponseDTO (Simulacao novaSimulacao){
        Produto produto = produtoService.getByCodigoProduto(novaSimulacao.getIdProduto());
        SimulacaoResponse dto = new SimulacaoResponse(novaSimulacao.getId(),
                novaSimulacao.getIdProduto(),
                produto.getNome(),
                produto.getTaxaJuros(),
                novaSimulacao.getPrazo(),
                novaSimulacao.getValorDesejado(),
                novaSimulacao.getValorTotalSac(),
                novaSimulacao.getValorTotalPrice()
        );

        List<SimulacaoParcelaDTO> listaSAC = new ArrayList<>();
        List<SimulacaoParcelaDTO> listaPrice = new ArrayList<>();

        for (SimulacaoParcela parcela : novaSimulacao.getParcelas()) {
            if (parcela.getTipoParcela() == null) {
                throw new RuntimeException("Tipo de parcela nulo");
            }
            if (parcela.getTipoParcela().equals(TipoParcelaEnum.SAC)) {
                SimulacaoParcelaDTO parcelaDTO = new SimulacaoParcelaDTO(
                        parcela.getNumeroParcela(),
                        parcela.getValorJuros(),
                        parcela.getValorAmortizacao(),
                        parcela.getValorPrestacao()
                );
                listaSAC.add(parcelaDTO);
            } else if (parcela.getTipoParcela().equals(TipoParcelaEnum.PRICE)) {
                SimulacaoParcelaDTO parcelaDTO = new SimulacaoParcelaDTO(
                        parcela.getNumeroParcela(),
                        parcela.getValorJuros(),
                        parcela.getValorAmortizacao(),
                        parcela.getValorPrestacao()
                );
                listaPrice.add(parcelaDTO);
            }
        }
        dto.setResultadoSimulacao(List.of(
                new SimulacaoTipoDTO("SAC", listaSAC),
                new SimulacaoTipoDTO("PRICE", listaPrice)
        ));
        return dto;
    }

}
