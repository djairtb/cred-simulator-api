package br.djair.caixa.exception;

import br.djair.caixa.dto.error.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.Context;
import lombok.extern.slf4j.Slf4j;

//uma padronizacao para respostas de erro no projeto  xD
@Provider
@Slf4j
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Exception exception) {
        int status = 500;
        String error = "Internal Server Error";

        if (exception instanceof jakarta.ws.rs.WebApplicationException webEx) {
            status = webEx.getResponse().getStatus();
            error = webEx.getClass().getSimpleName();
        }
        ErrorResponse response = new ErrorResponse(
                status,
                error,
                exception.getMessage(),
                uriInfo != null ? uriInfo.getPath() : "N/A"
        );

        log.error("Erro na requisição [{}]: {}", uriInfo != null ? uriInfo.getPath() : "N/A", exception.getMessage(), exception);

        return Response.status(status)
                .entity(response)
                .build();
    }
}
