package ru.yandex.practicum.collector.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.kafka.EventProducer;
import ru.yandex.practicum.collector.mapper.GrpcEventMapper;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Service
public class CollectorService {

    private final EventProducer producer;
    private final GrpcEventMapper mapper;
    private final String sensorTopic;
    private final String hubTopic;

    public CollectorService(EventProducer producer,
                            GrpcEventMapper mapper,
                            @Value("${collector.kafka.topic.sensors}") String sensorTopic,
                            @Value("${collector.kafka.topic.hubs}") String hubTopic) {
        this.producer = producer;
        this.mapper = mapper;
        this.sensorTopic = sensorTopic;
        this.hubTopic = hubTopic;
    }

    public void collectSensorEvent(SensorEventProto event) {
        producer.send(sensorTopic, event.getHubId(), mapper.toAvro(event));
    }

    public void collectHubEvent(HubEventProto event) {
        producer.send(hubTopic, event.getHubId(), mapper.toAvro(event));
    }
}