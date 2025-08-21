package br.djair.caixa.resource;

import br.djair.caixa.dto.simulacao.SimulacaoParcelaDTO;
import br.djair.caixa.dto.simulacao.SimulacaoRequest;
import br.djair.caixa.dto.simulacao.SimulacaoResponse;
import br.djair.caixa.dto.simulacao.SimulacaoTipoDTO;
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

        return Response.ok(dto).build();
    }

//    @GET
//    public List<SimulacaoResponse> listarTodas() {
//        return simulacaoService.listarTodas();
//    }
}
