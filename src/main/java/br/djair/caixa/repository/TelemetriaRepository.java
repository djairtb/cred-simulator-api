package br.djair.caixa.repository;

import br.djair.caixa.model.simulacao.Telemetria;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class TelemetriaRepository implements PanacheRepository<Telemetria> {
    public Telemetria findByEndpointAndData(String endpoint, LocalDate data) {
        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.atTime(23, 59, 59);

        return find("nomeEndpoint = ?1 and dataCriacao between ?2 and ?3", endpoint, inicio, fim).firstResult();
    }
    public List<Telemetria> listByData(LocalDate data) {
        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.atTime(23, 59, 59);

        return find("dataCriacao BETWEEN ?1 AND ?2", inicio, fim).list();
    }


}
