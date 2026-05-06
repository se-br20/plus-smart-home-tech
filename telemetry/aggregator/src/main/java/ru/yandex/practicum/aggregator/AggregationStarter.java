package ru.yandex.practicum.aggregator;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.aggregator.service.SnapshotService;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;

@Component
public class AggregationStarter {

    private final KafkaConsumer<String, SensorEventAvro> consumer;
    private final KafkaProducer<String, SpecificRecordBase> producer;
    private final SnapshotService snapshotService;
    private final String sensorTopic;
    private final String snapshotTopic;

    public AggregationStarter(KafkaConsumer<String, SensorEventAvro> consumer,
                              KafkaProducer<String, SpecificRecordBase> producer,
                              SnapshotService snapshotService,
                              @Value("${aggregator.kafka.topic.sensors}") String sensorTopic,
                              @Value("${aggregator.kafka.topic.snapshots}") String snapshotTopic) {
        this.consumer = consumer;
        this.producer = producer;
        this.snapshotService = snapshotService;
        this.sensorTopic = sensorTopic;
        this.snapshotTopic = snapshotTopic;
    }

    public void start() {
        try {
            consumer.subscribe(List.of(sensorTopic));

            while (true) {
                var records = consumer.poll(Duration.ofMillis(1000));

                for (var record : records) {
                    SensorEventAvro event = record.value();

                    snapshotService.updateState(event)
                            .ifPresent(snapshot -> sendSnapshot(snapshotTopic, snapshot));
                }

                consumer.commitSync();
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            System.err.println("Ошибка во время обработки событий от датчиков");
            e.printStackTrace();
        } finally {
            try {
                producer.flush();
                consumer.commitSync();
            } catch (Exception e) {
                System.err.println("Ошибка при flush/commit перед закрытием");
                e.printStackTrace();
            } finally {
                consumer.close();
                producer.close(Duration.ofSeconds(10));
            }
        }
    }

    private void sendSnapshot(String topic, SensorsSnapshotAvro snapshot) {
        ProducerRecord<String, SpecificRecordBase> record =
                new ProducerRecord<>(topic, snapshot.getHubId(), snapshot);

        producer.send(record);
    }
}
