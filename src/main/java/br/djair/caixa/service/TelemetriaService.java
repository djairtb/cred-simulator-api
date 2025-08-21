package br.djair.caixa.service;

import br.djair.caixa.model.simulacao.Telemetria;
import br.djair.caixa.repository.TelemetriaRepository;
import br.djair.caixa.store.TelemetriaMinTimeStore;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class TelemetriaService {
    @Inject
    TelemetriaRepository repository;
    @Inject
    MeterRegistry registry;
    @Inject
    TelemetriaMinTimeStore telemetriaMinTimeStore;

    public List<Telemetria> listarTelemetriaEmMemoria() {
        List<Telemetria> telemetrias = new ArrayList<>();
        for (Meter meter : registry.getMeters()) {
            if (!(meter instanceof Timer)) {
                continue;
            }
            String endpoint = meter.getId().getTag("endpoint");
            if (endpoint == null) {
                continue;
            }

            Timer timer = (Timer) meter;
            Telemetria t = new Telemetria();
            t.setNomeEndpoint(endpoint);
            t.setMinimoMilissegundos(telemetriaMinTimeStore.getMin(endpoint));
            t.setTotalMilissegundos((long) timer.totalTime(TimeUnit.MILLISECONDS));
            t.setMaximoMilissegundos(Double.isNaN(timer.max(TimeUnit.MILLISECONDS)) ? 0 : (long) timer.max(TimeUnit.MILLISECONDS));
            t.setMediaMilissegundos(Double.isNaN(timer.mean(TimeUnit.MILLISECONDS)) ? 0 : (long) timer.mean(TimeUnit.MILLISECONDS));
            t.setTotalChamadas((int) timer.count());

            Counter successCounter = registry.find(timer.getId().getName() + ".success")
                    .tag("endpoint", timer.getId().getTag("endpoint"))
                    .counter();
            t.setSucessoChamadas(successCounter != null ? (int) successCounter.count() : 0);

            telemetrias.add(t);
        }
        return telemetrias;
    }

    public List<Telemetria> listarPorData(LocalDate data){
        return  repository.listByData(data);
    }

    public List<Telemetria> consolidarTelemetrias() {
        this.persistirMetricas();
        List<Telemetria> telemetrias = repository.listAll();
        Map<String, Telemetria> consolidadas = new HashMap<>();

        for (Telemetria t : telemetrias) {
            consolidadas.compute(t.getNomeEndpoint(), (endpoint, existente) -> {
                if (existente == null) {
                    // cria uma cOpia inicial
                    Telemetria novo = new Telemetria();
                    novo.setNomeEndpoint(t.getNomeEndpoint());
                    novo.setTotalMilissegundos(t.getTotalMilissegundos());
                    novo.setMaximoMilissegundos(t.getMaximoMilissegundos());
                    novo.setMediaMilissegundos(t.getMediaMilissegundos());
                    novo.setTotalChamadas(t.getTotalChamadas());
                    novo.setSucessoChamadas(t.getSucessoChamadas());
                    return novo;
                } else {
                    // consolida os valors
                    existente.setTotalMilissegundos(existente.getTotalMilissegundos() + t.getTotalMilissegundos());
                    existente.setMaximoMilissegundos(Math.max(existente.getMaximoMilissegundos(), t.getMaximoMilissegundos()));
                    existente.setTotalChamadas(existente.getTotalChamadas() + t.getTotalChamadas());
                    existente.setSucessoChamadas(existente.getSucessoChamadas() + t.getSucessoChamadas());

                    //mEdia ponderada
                    long novaMedia = existente.getTotalMilissegundos() / Math.max(1, existente.getTotalChamadas());
                    existente.setMediaMilissegundos(novaMedia);

                    return existente;
                }
            });
        }
        return new ArrayList<>(consolidadas.values());
    }

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

            LocalDate hoje = LocalDate.now();
            Timer timer = (Timer) meter;

            long total = (long) timer.totalTime(TimeUnit.MILLISECONDS);
            long max = Double.isNaN(timer.max(TimeUnit.MILLISECONDS)) ? 0 : (long) timer.max(TimeUnit.MILLISECONDS);
            long mean = Double.isNaN(timer.mean(TimeUnit.MILLISECONDS)) ? 0 : (long) timer.mean(TimeUnit.MILLISECONDS);
            Integer count = (int) timer.count();

            Counter successCounter = registry.find(timer.getId().getName() + ".success")
                    .tag("endpoint", timer.getId().getTag("endpoint"))
                    .counter();
            Integer sucessoCount = (successCounter != null) ? (int) successCounter.count() : 0;

            Telemetria existente = repository.findByEndpointAndData(endpoint, hoje);

            if (existente != null) {
                existente.setTotalMilissegundos(total);
                existente.setMaximoMilissegundos(max);
                existente.setMinimoMilissegundos(telemetriaMinTimeStore.getMin(endpoint));
                existente.setMediaMilissegundos(mean);
                existente.setTotalChamadas(count);
                existente.setSucessoChamadas(sucessoCount);
                existente.setDataCriacao(LocalDateTime.now());

                repository.getEntityManager().merge(existente);
            } else {
                Telemetria t = new Telemetria();
                t.setNomeEndpoint(endpoint);
                t.setTotalMilissegundos(total);
                t.setMaximoMilissegundos(max);
                t.setMediaMilissegundos(mean);
                t.setTotalChamadas(count);
                t.setSucessoChamadas(sucessoCount);
                t.setDataCriacao(LocalDateTime.now());

                repository.persist(t);
            }
        }
    }

    /**
     Esse carinha estarta os counters de metricas para cada endpoint ja persitido no banco.
     Fiz isso pq o Micrometer so persiste a telemetria em memoria, e quando o servidor reinicia
     os counters ficam zerados.
     **/
    @Transactional
    public void inicializarCounters() {
        LocalDate hoje = LocalDate.now();
        for (Telemetria t : repository.listByData(hoje)) {
            telemetriaMinTimeStore.updateMin(t.getNomeEndpoint(), t.getMinimoMilissegundos());
            Counter.builder(t.getNomeEndpoint() + ".success")
                    .tag("endpoint", t.getNomeEndpoint())
                    .register(registry)
                    .increment(t.getSucessoChamadas());
        }
    }

    /**
     Esse outro carinha zera as metricas as 00:00 de
     todo dia para melhor viusualizacao
     **/
    @Transactional
    public void resetarTodasMetricas() {
        telemetriaMinTimeStore.limparStore();
        for (Meter meter : registry.getMeters()) {
            String nome = meter.getId().getName();
            var tags = meter.getId().getTags();
            registry.remove(meter);
            switch (meter.getId().getType()) {
                case COUNTER:
                    Counter.builder(nome)
                            .tags(tags)
                            .register(registry);
                    break;
                case TIMER:
                    Timer.builder(nome)
                            .tags(tags)
                            .register(registry);
                    break;
                default:
                    break;
            }
        }
    }
}