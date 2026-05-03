package ru.yandex.practicum.collector.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.dto.DeviceAction;
import ru.yandex.practicum.collector.dto.DeviceAddedEvent;
import ru.yandex.practicum.collector.dto.DeviceRemovedEvent;
import ru.yandex.practicum.collector.dto.HubEvent;
import ru.yandex.practicum.collector.dto.ScenarioAddedEvent;
import ru.yandex.practicum.collector.dto.ScenarioCondition;
import ru.yandex.practicum.collector.dto.ScenarioRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.time.Instant;
import java.util.List;

@Component
public class HubEventMapper {

    public HubEventAvro toAvro(HubEvent event) {
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp() == null ? Instant.now() : event.getTimestamp())
                .setPayload(toPayload(event))
                .build();
    }

    private Object toPayload(HubEvent event) {
        if (event instanceof DeviceAddedEvent deviceAddedEvent) {
            return DeviceAddedEventAvro.newBuilder()
                    .setId(deviceAddedEvent.getId())
                    .setType(DeviceTypeAvro.valueOf(deviceAddedEvent.getDeviceType().name()))
                    .build();
        }

        if (event instanceof DeviceRemovedEvent deviceRemovedEvent) {
            return DeviceRemovedEventAvro.newBuilder()
                    .setId(deviceRemovedEvent.getId())
                    .build();
        }

        if (event instanceof ScenarioAddedEvent scenarioAddedEvent) {
            return ScenarioAddedEventAvro.newBuilder()
                    .setName(scenarioAddedEvent.getName())
                    .setConditions(toConditionAvroList(scenarioAddedEvent.getConditions()))
                    .setActions(toActionAvroList(scenarioAddedEvent.getActions()))
                    .build();
        }

        if (event instanceof ScenarioRemovedEvent scenarioRemovedEvent) {
            return ScenarioRemovedEventAvro.newBuilder()
                    .setName(scenarioRemovedEvent.getName())
                    .build();
        }

        throw new IllegalArgumentException("Unknown hub event type: " + event.getClass());
    }

    private List<ScenarioConditionAvro> toConditionAvroList(List<ScenarioCondition> conditions) {
        return conditions.stream()
                .map(condition -> ScenarioConditionAvro.newBuilder()
                        .setSensorId(condition.getSensorId())
                        .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                        .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
                        .setValue(condition.getValue())
                        .build())
                .toList();
    }

    private List<DeviceActionAvro> toActionAvroList(List<DeviceAction> actions) {
        return actions.stream()
                .map(action -> DeviceActionAvro.newBuilder()
                        .setSensorId(action.getSensorId())
                        .setType(ActionTypeAvro.valueOf(action.getType().name()))
                        .setValue(action.getValue())
                        .build())
                .toList();
    }
}