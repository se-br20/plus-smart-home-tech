package ru.yandex.practicum.collector.config;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.serializer.AvroSerializer;

import java.util.Properties;

@Configuration
public class KafkaConfig {

    @Bean
    public KafkaProducer<String, SpecificRecordBase> kafkaProducer(
            @Value("${collector.kafka.bootstrap-servers:localhost:9092}") String bootstrapServers
    ) {
        Properties properties = new Properties();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroSerializer.class.getName());

        properties.put(ProducerConfig.ACKS_CONFIG, "1");
        properties.put(ProducerConfig.LINGER_MS_CONFIG, "10");
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, "32768");

        return new KafkaProducer<>(properties);
    }
}
