package br.djair.caixa.resource;

import br.djair.caixa.dto.simulacao.*;
import br.djair.caixa.interceptor.TelemetriaNotation;
import br.djair.caixa.model.produto.Produto;
import br.djair.caixa.model.simulacao.Simulacao;
import br.djair.caixa.model.simulacao.SimulacaoParcela;
import br.djair.caixa.model.enums.TipoParcelaEnum;
import br.djair.caixa.service.ProdutoService;
import br.djair.caixa.service.simulacao.SimulacaoService;
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
    SimulacaoService simulacaoService;
    @Inject
    ProdutoService produtoService;

    @POST
    public Response simular(SimulacaoRequest request) {
        Simulacao novaSimulacao = simulacaoService.simular(request.getValorDesejado(), request.getPrazo());
        SimulacaoResponse dto = geraSimualcaoResponseDTO(novaSimulacao);
        return Response.ok(dto).build();
    }

    @GET
    public Response listarTodas(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Paginacao incorreta");
        }
        List<Simulacao> simulacoes = simulacaoService.listarTodas();
        int totalElements = simulacoes.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        if (page >= totalPages) {
            throw new WebApplicationException(
                    "pagina fora do limite x.x Total de páginas disponíveis: " + totalPages,
                    Response.Status.BAD_REQUEST
            );
        }

        int fromIndex = page * size;
        if (fromIndex >= totalElements) {
            fromIndex = Math.max(totalElements - size, 0);
        }
        int toIndex = Math.min(fromIndex + size, totalElements);
        List<Simulacao> content = simulacoes.subList(fromIndex, toIndex);

        List<SimulacaoResponse> dtos = new ArrayList<>();
        for(Simulacao simulacao : content) {
            dtos.add(geraSimualcaoResponseDTO(simulacao));
        }

        SimulacaoPaginadaResponse paginadaResponse = new SimulacaoPaginadaResponse(page,size,totalPages,totalElements,dtos);
        return Response.ok(paginadaResponse).build();
    }


    //GERADORES DE DTO
    public SimulacaoResponse geraSimualcaoResponseDTO (Simulacao novaSimulacao){
        Produto produto = produtoService.getByCodigoProduto(novaSimulacao.getIdProduto());
        SimulacaoResponse dto = new SimulacaoResponse();
        dto.setIdSimulacao(novaSimulacao.getId());
        dto.setCodigoProduto(novaSimulacao.getIdProduto());
        dto.setDescricaoProduto(produto.getNome());
        dto.setTaxaJuros(produto.getTaxaJuros());


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
