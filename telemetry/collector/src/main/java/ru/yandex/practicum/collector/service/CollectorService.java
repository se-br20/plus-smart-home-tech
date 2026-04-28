package ru.yandex.practicum.collector.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.dto.HubEvent;
import ru.yandex.practicum.collector.dto.SensorEvent;
import ru.yandex.practicum.collector.kafka.EventProducer;
import ru.yandex.practicum.collector.mapper.HubEventMapper;
import ru.yandex.practicum.collector.mapper.SensorEventMapper;

@Service
public class CollectorService {

    private final EventProducer producer;
    private final SensorEventMapper sensorEventMapper;
    private final HubEventMapper hubEventMapper;
    private final String sensorTopic;
    private final String hubTopic;

    public CollectorService(EventProducer producer,
                            SensorEventMapper sensorEventMapper,
                            HubEventMapper hubEventMapper,
                            @Value("${collector.kafka.topic.sensors}") String sensorTopic,
                            @Value("${collector.kafka.topic.hubs}") String hubTopic) {
        this.producer = producer;
        this.sensorEventMapper = sensorEventMapper;
        this.hubEventMapper = hubEventMapper;
        this.sensorTopic = sensorTopic;
        this.hubTopic = hubTopic;
    }

    public void collectSensorEvent(SensorEvent event) {
        producer.send(sensorTopic, event.getHubId(), sensorEventMapper.toAvro(event));
    }

    public void collectHubEvent(HubEvent event) {
        producer.send(hubTopic, event.getHubId(), hubEventMapper.toAvro(event));
    }
}