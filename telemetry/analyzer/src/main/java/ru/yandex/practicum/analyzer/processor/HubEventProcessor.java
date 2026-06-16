package ru.yandex.practicum.analyzer.processor;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.service.HubEventService;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.List;

@Component
public class HubEventProcessor implements Runnable {

    private final KafkaConsumer<String, HubEventAvro> consumer;
    private final HubEventService hubEventService;
    private final String topic;

    public HubEventProcessor(KafkaConsumer<String, HubEventAvro> consumer,
                             HubEventService hubEventService,
                             @Value("${analyzer.kafka.topic.hubs}") String topic) {
        this.consumer = consumer;
        this.hubEventService = hubEventService;
        this.topic = topic;
    }

    @Override
    public void run() {
        try {
            consumer.subscribe(List.of(topic));

            while (true) {
                var records = consumer.poll(Duration.ofMillis(1000));

                records.forEach(record -> hubEventService.handle(record.value()));

                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            System.err.println("Ошибка при обработке hub events");
            e.printStackTrace();
        } finally {
            try {
                consumer.commitSync();
            } catch (Exception e) {
                System.err.println("Ошибка commitSync при закрытии consumer");
                e.printStackTrace();
            } finally {
                consumer.close();
            }
        }
    }

    public void stop() {
        consumer.wakeup();
    }
}
