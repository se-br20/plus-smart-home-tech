package ru.yandex.practicum.collector.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.dto.HubEvent;
import ru.yandex.practicum.collector.dto.SensorEvent;
import ru.yandex.practicum.collector.kafka.EventProducer;
import ru.yandex.practicum.collector.mapper.HubEventMapper;
import ru.yandex.practicum.collector.mapper.SensorEventMapper;

@Service
public class CollectorService {

    private static final String SENSOR_TOPIC = "telemetry.sensors.v1";
    private static final String HUB_TOPIC = "telemetry.hubs.v1";

    private final EventProducer producer;
    private final SensorEventMapper sensorEventMapper;
    private final HubEventMapper hubEventMapper;

    public CollectorService(EventProducer producer,
                            SensorEventMapper sensorEventMapper,
                            HubEventMapper hubEventMapper) {
        this.producer = producer;
        this.sensorEventMapper = sensorEventMapper;
        this.hubEventMapper = hubEventMapper;
    }

    public void collectSensorEvent(SensorEvent event) {
        producer.send(SENSOR_TOPIC, event.getHubId(), sensorEventMapper.toAvro(event));
    }

    public void collectHubEvent(HubEvent event) {
        producer.send(HUB_TOPIC, event.getHubId(), hubEventMapper.toAvro(event));
    }
}