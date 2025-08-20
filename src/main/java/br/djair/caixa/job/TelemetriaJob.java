package br.djair.caixa.job;

import br.djair.caixa.model.Telemetria;
import br.djair.caixa.repository.TelemetriaRepository;
import br.djair.caixa.service.TelemetriaService;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.scheduler.Scheduled;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@ApplicationScoped

public class TelemetriaJob {


    @Inject
    TelemetriaService telemetriaService;

    @Scheduled(every = "42s") // persiste metricas a cada 42 segundos, 42 é a resposta para a vida...
    @Transactional
    public void agendarPersistirMetricas() {
        telemetriaService.persistirMetricas();
    }

    @PostConstruct
    @Transactional
    public void agendaInicializarCounters() {
        telemetriaService.inicializarCounters();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void resetarTodasMetricas() {
        telemetriaService.resetarTodasMetricas();
    }
}
