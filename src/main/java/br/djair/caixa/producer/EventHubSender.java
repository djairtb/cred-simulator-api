package br.djair.caixa.producer;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventDataBatch;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EventHubSender {

    private final EventHubProducerClient producer;

    public EventHubSender() {
        this.producer = new EventHubClientBuilder()
                .connectionString("<CONNECTION_STRING>", "<EVENT_HUB_NAME>")
                .buildProducerClient();
    }

    public void sendMessage(String message) {
        EventDataBatch batch = producer.createBatch();
        batch.tryAdd(new EventData(message));
        producer.send(batch);
    }

    public void close() {
        producer.close();
    }
}
