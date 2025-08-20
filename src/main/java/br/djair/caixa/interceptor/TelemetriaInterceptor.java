package br.djair.caixa.interceptor;

import br.djair.caixa.store.TelemetriaMinTimeStore;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;


//trata as notacao q foi capturada na interface do outro arquivo

// 2. Interceptor
@TelemetriaNotation
@Interceptor
public class TelemetriaInterceptor {
    @Inject
    TelemetriaMinTimeStore minTimeStore;

    @Inject
    MeterRegistry registry;

    @Context
    UriInfo uriInfo;

    @AroundInvoke
    public Object around(InvocationContext ctx) throws Exception {
        String endpoint = uriInfo.getPath();
        Timer.Sample sample = Timer.start(registry);

        try {
            Object result = ctx.proceed(); // executa o método real
            somaSucesso(endpoint);
            return result;
        } catch (Exception e) {
            //quando falha n faz nada pq só temos contador de sucesso
            throw e;
        } finally {
            pararTimer(endpoint, sample);
        }
    }

    private void somaSucesso(String endpoint) {
        Counter.builder("http.server.requests.success")
                .tag("endpoint", endpoint)
                .register(registry)
                .increment();
    }

    private void pararTimer(String endpoint, Timer.Sample sample) {
        long durationNs = sample.stop(Timer.builder("http.server.requests")
                .tag("endpoint", endpoint)
                .register(registry));
        long durationMs = durationNs / 1_000_000;
        minTimeStore.updateMin(endpoint,durationMs);
    }
}
