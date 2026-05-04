package ru.yandex.practicum.collector.mapper;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;

@Component
public class GrpcEventMapper {

    public SensorEventAvro toAvro(SensorEventProto event) {
        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(toInstant(event.getTimestamp()))
                .setPayload(toSensorPayload(event))
                .build();
    }

    public HubEventAvro toAvro(HubEventProto event) {
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(toInstant(event.getTimestamp()))
                .setPayload(toHubPayload(event))
                .build();
    }

    private Object toSensorPayload(SensorEventProto event) {
        return switch (event.getPayloadCase()) {
            case MOTION_SENSOR -> {
                MotionSensorProto payload = event.getMotionSensor();

                yield MotionSensorAvro.newBuilder()
                        .setLinkQuality(payload.getLinkQuality())
                        .setMotion(payload.getMotion())
                        .setVoltage(payload.getVoltage())
                        .build();
            }

            case TEMPERATURE_SENSOR -> {
                TemperatureSensorProto payload = event.getTemperatureSensor();

                yield TemperatureSensorAvro.newBuilder()
                        .setTemperatureC(payload.getTemperatureC())
                        .setTemperatureF(payload.getTemperatureF())
                        .build();
            }

            case LIGHT_SENSOR -> {
                LightSensorProto payload = event.getLightSensor();

                yield LightSensorAvro.newBuilder()
                        .setLinkQuality(payload.getLinkQuality())
                        .setLuminosity(payload.getLuminosity())
                        .build();
            }

            case CLIMATE_SENSOR -> {
                ClimateSensorProto payload = event.getClimateSensor();

                yield ClimateSensorAvro.newBuilder()
                        .setTemperatureC(payload.getTemperatureC())
                        .setHumidity(payload.getHumidity())
                        .setCo2Level(payload.getCo2Level())
                        .build();
            }

            case SWITCH_SENSOR -> {
                SwitchSensorProto payload = event.getSwitchSensor();

                yield SwitchSensorAvro.newBuilder()
                        .setState(payload.getState())
                        .build();
            }

            case PAYLOAD_NOT_SET -> throw new IllegalArgumentException("Sensor payload is not set");
        };
    }

    private Object toHubPayload(HubEventProto event) {
        return switch (event.getPayloadCase()) {
            case DEVICE_ADDED -> DeviceAddedEventAvro.newBuilder()
                    .setId(event.getDeviceAdded().getId())
                    .setType(DeviceTypeAvro.valueOf(event.getDeviceAdded().getType().name()))
                    .build();

            case DEVICE_REMOVED -> DeviceRemovedEventAvro.newBuilder()
                    .setId(event.getDeviceRemoved().getId())
                    .build();

            case SCENARIO_ADDED -> ScenarioAddedEventAvro.newBuilder()
                    .setName(event.getScenarioAdded().getName())
                    .setConditions(event.getScenarioAdded().getConditionList().stream()
                            .map(this::toAvro)
                            .toList())
                    .setActions(event.getScenarioAdded().getActionList().stream()
                            .map(this::toAvro)
                            .toList())
                    .build();

            case SCENARIO_REMOVED -> ScenarioRemovedEventAvro.newBuilder()
                    .setName(event.getScenarioRemoved().getName())
                    .build();

            case PAYLOAD_NOT_SET -> throw new IllegalArgumentException("Hub payload is not set");
        };
    }

    private ScenarioConditionAvro toAvro(ScenarioConditionProto condition) {
        Object value = switch (condition.getValueCase()) {
            case BOOL_VALUE -> condition.getBoolValue();
            case INT_VALUE -> condition.getIntValue();
            case VALUE_NOT_SET -> null;
        };

        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
                .setValue(value)
                .build();
    }

    private DeviceActionAvro toAvro(DeviceActionProto action) {
        Integer value = action.hasValue() ? action.getValue() : null;

        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(ActionTypeAvro.valueOf(action.getType().name()))
                .setValue(value)
                .build();
    }

    private Instant toInstant(Timestamp timestamp) {
        if (timestamp == null || timestamp.getSeconds() == 0 && timestamp.getNanos() == 0) {
            return Instant.now();
        }

        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}
