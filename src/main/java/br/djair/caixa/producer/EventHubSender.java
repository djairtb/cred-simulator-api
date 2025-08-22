package br.djair.caixa.producer;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventDataBatch;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Slf4j
public class EventHubSender {

    private final EventHubProducerClient producer;

    public EventHubSender(
            @ConfigProperty(name = "eventhub.connection-string") String connectionString,
            @ConfigProperty(name = "eventhub.name") String eventHubName) {

        this.producer = new EventHubClientBuilder()
                .connectionString(connectionString, eventHubName)
                .buildProducerClient();
    }

    public void sendMessage(String message) {
        EventDataBatch batch = producer.createBatch();
        batch.tryAdd(new EventData(message));
        producer.send(batch);
        log.info("Mensagem enviada para o Event Hub!");
    }

    //para n ficar na lerdeza na primeira chamada de api
    public void ligarMotores() {
        try {
            EventDataBatch batch = producer.createBatch();
            batch.tryAdd(new EventData("warmup-message"));
            producer.send(batch);
            log.info("PING!");
        } catch (Exception e) {
            log.warn("Falha Erro ao dar tranco no EventHub: {}", e.getMessage());
        }
    }



    public void close() {
        producer.close();
    }
}
