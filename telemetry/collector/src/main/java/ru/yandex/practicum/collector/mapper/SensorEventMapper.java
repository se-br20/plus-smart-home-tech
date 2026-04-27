package ru.yandex.practicum.collector.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.ClimateSensorEvent;
import ru.yandex.practicum.collector.dto.LightSensorEvent;
import ru.yandex.practicum.collector.dto.MotionSensorEvent;
import ru.yandex.practicum.collector.dto.SensorEvent;
import ru.yandex.practicum.collector.dto.SwitchSensorEvent;
import ru.yandex.practicum.collector.dto.TemperatureSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

import java.time.Instant;

@Component
public class SensorEventMapper {

    public SensorEventAvro toAvro(SensorEvent event) {
        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp() == null ? Instant.now() : event.getTimestamp())
                .setPayload(toPayload(event))
                .build();
    }

    private Object toPayload(SensorEvent event) {
        if (event instanceof ClimateSensorEvent climateEvent) {
            return ClimateSensorAvro.newBuilder()
                    .setTemperatureC(climateEvent.getTemperatureC())
                    .setHumidity(climateEvent.getHumidity())
                    .setCo2Level(climateEvent.getCo2Level())
                    .build();
        }

        if (event instanceof LightSensorEvent lightEvent) {
            return LightSensorAvro.newBuilder()
                    .setLinkQuality(lightEvent.getLinkQuality() == null ? 0 : lightEvent.getLinkQuality())
                    .setLuminosity(lightEvent.getLuminosity() == null ? 0 : lightEvent.getLuminosity())
                    .build();
        }

        if (event instanceof MotionSensorEvent motionEvent) {
            return MotionSensorAvro.newBuilder()
                    .setLinkQuality(motionEvent.getLinkQuality())
                    .setMotion(motionEvent.getMotion())
                    .setVoltage(motionEvent.getVoltage())
                    .build();
        }

        if (event instanceof SwitchSensorEvent switchEvent) {
            return SwitchSensorAvro.newBuilder()
                    .setState(switchEvent.getState())
                    .build();
        }

        if (event instanceof TemperatureSensorEvent temperatureEvent) {
            return TemperatureSensorAvro.newBuilder()
                    .setTemperatureC(temperatureEvent.getTemperatureC())
                    .setTemperatureF(temperatureEvent.getTemperatureF())
                    .build();
        }

        throw new IllegalArgumentException("Unknown sensor event type: " + event.getClass());
    }
}