package br.djair.caixa.store;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ApplicationScoped
public class TelemetriaMinTimeStore {
    private final ConcurrentMap<String, Long> minTimeMap = new ConcurrentHashMap<>();

    public void updateMin(String endpoint, long durationMs) {
        minTimeMap.merge(endpoint, durationMs, Math::min);
    }

    public Long getMin(String endpoint) {
        return minTimeMap.get(endpoint);
    }

    public void limparStore() {
        minTimeMap.clear();
    }
}
