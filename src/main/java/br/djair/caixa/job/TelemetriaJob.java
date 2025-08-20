package br.djair.caixa.job;

import br.djair.caixa.model.Telemetria;
import br.djair.caixa.repository.TelemetriaRepository;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.scheduler.Scheduled;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class TelemetriaJob {

    @Inject
    MeterRegistry registry;

    @Inject
    TelemetriaRepository repository;

    @Scheduled(every = "42s") // persiste metricas a cada 42 segundos, 42 é a resposta para a vida...
    @Transactional
    public void persistirMetricas() {
        for (Meter meter : registry.getMeters()) {
            if (!(meter instanceof Timer)) {
                continue;
            }
            String endpoint = meter.getId().getTag("endpoint");
            if (endpoint == null) {
                continue;
            }


            Timer timer = (Timer) meter;

            long total = (long) timer.totalTime(TimeUnit.MILLISECONDS);
            long max = Double.isNaN(timer.max(TimeUnit.MILLISECONDS)) ? 0 : (long) timer.max(TimeUnit.MILLISECONDS);
            long mean = Double.isNaN(timer.mean(TimeUnit.MILLISECONDS)) ? 0 : (long) timer.mean(TimeUnit.MILLISECONDS);
            long count = timer.count();

            // Recupera contador de sucesso seguro
            Counter successCounter = registry.find(timer.getId().getName() + ".success")
                    .tag("endpoint", timer.getId().getTag("endpoint"))
                    .counter();
            long sucessoCount = (successCounter != null) ? (long) successCounter.count() : 0;

            Telemetria t = new Telemetria();
            t.setNomeEndpoint(timer.getId().getName());
            t.setTotalMilissegundos(total);
            t.setMaximoMilissegundos(max);
            t.setMediaMilissegundos(mean);
            t.setTotalChamadas(count);
            t.setSucessoChamadas(sucessoCount);
            t.setDataCriacao(LocalDateTime.now());

            repository.persist(t);
        }
    }

    /**
     Esse carinha estarta os counters de metricas para cada endpoint ja persitido no banco.
     Fiz isso pq o Micrometer so persiste a telemetria em memoria, e quando o servidor reinicia
     os counters ficam zerados.
     **/
    @PostConstruct
    @Transactional
    public void inicializarCounters() {
        for (Telemetria t : repository.listAll()) {
            Counter.builder(t.getNomeEndpoint() + ".success")
                    .tag("endpoint", t.getNomeEndpoint())
                    .register(registry)
                    .increment(t.getSucessoChamadas());
        }
    }
}
