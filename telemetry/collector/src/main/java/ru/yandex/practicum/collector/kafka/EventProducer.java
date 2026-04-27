package ru.yandex.practicum.collector.kafka;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {

    private final KafkaProducer<String, SpecificRecordBase> producer;

    public EventProducer(KafkaProducer<String, SpecificRecordBase> producer) {
        this.producer = producer;
    }

    public void send(String topic, String key, SpecificRecordBase event) {
        ProducerRecord<String, SpecificRecordBase> record =
                new ProducerRecord<>(topic, key, event);

        producer.send(record);
        producer.flush();
    }
}