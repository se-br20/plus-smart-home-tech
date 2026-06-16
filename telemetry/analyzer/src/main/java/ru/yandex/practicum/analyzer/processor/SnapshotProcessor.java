package ru.yandex.practicum.analyzer.processor;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.service.SnapshotService;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;

@Component
public class SnapshotProcessor {

    private final KafkaConsumer<String, SensorsSnapshotAvro> consumer;
    private final SnapshotService snapshotService;
    private final String topic;

    public SnapshotProcessor(KafkaConsumer<String, SensorsSnapshotAvro> consumer,
                             SnapshotService snapshotService,
                             @Value("${analyzer.kafka.topic.snapshots}") String topic) {
        this.consumer = consumer;
        this.snapshotService = snapshotService;
        this.topic = topic;
    }

    public void start() {
        try {
            consumer.subscribe(List.of(topic));

            while (true) {
                var records = consumer.poll(Duration.ofMillis(1000));

                records.forEach(record -> snapshotService.handleSnapshot(record.value()));

                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            System.err.println("Ошибка при обработке snapshots");
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