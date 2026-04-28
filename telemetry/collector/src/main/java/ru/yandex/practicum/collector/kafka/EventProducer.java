package ru.yandex.practicum.collector.kafka;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class EventProducer implements AutoCloseable {

    private final KafkaProducer<String, SpecificRecordBase> producer;

    public EventProducer(KafkaProducer<String, SpecificRecordBase> producer) {
        this.producer = producer;
    }

    public void send(String topic, String key, SpecificRecordBase value) {
        ProducerRecord<String, SpecificRecordBase> record =
                new ProducerRecord<>(topic, key, value);

        producer.send(record);
    }

    @Override
    public void close() {
        producer.flush();
        producer.close(Duration.ofSeconds(10));
    }
}
