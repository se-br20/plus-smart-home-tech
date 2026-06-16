package ru.yandex.practicum.analyzer.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.analyzer.model.Condition;
import ru.yandex.practicum.analyzer.model.Scenario;
import ru.yandex.practicum.analyzer.model.ScenarioConditionLink;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Service
public class ScenarioEvaluator {

    public boolean matches(Scenario scenario, SensorsSnapshotAvro snapshot) {
        if (scenario.getConditions() == null || scenario.getConditions().isEmpty()) {
            return false;
        }

        return scenario.getConditions().stream()
                .allMatch(conditionLink -> checkCondition(conditionLink, snapshot));
    }

    private boolean checkCondition(ScenarioConditionLink link, SensorsSnapshotAvro snapshot) {
        SensorStateAvro state = snapshot.getSensorsState().get(link.getSensor().getId());

        if (state == null) {
            return false;
        }

        Integer currentValue = extractValue(state.getData(), link.getCondition().getType());

        if (currentValue == null) {
            return false;
        }

        return compare(currentValue, link.getCondition());
    }

    private Integer extractValue(Object data, String type) {
        return switch (type) {
            case "MOTION" -> data instanceof MotionSensorAvro motion
                    ? booleanToInt(motion.getMotion())
                    : null;

            case "LUMINOSITY" -> data instanceof LightSensorAvro light
                    ? light.getLuminosity()
                    : null;

            case "SWITCH" -> data instanceof SwitchSensorAvro switchSensor
                    ? booleanToInt(switchSensor.getState())
                    : null;

            case "TEMPERATURE" -> {
                if (data instanceof TemperatureSensorAvro temperature) {
                    yield temperature.getTemperatureC();
                }
                if (data instanceof ClimateSensorAvro climate) {
                    yield climate.getTemperatureC();
                }
                yield null;
            }

            case "HUMIDITY" -> data instanceof ClimateSensorAvro climate
                    ? climate.getHumidity()
                    : null;

            case "CO2LEVEL" -> data instanceof ClimateSensorAvro climate
                    ? climate.getCo2Level()
                    : null;

            default -> null;
        };
    }

    private int booleanToInt(boolean value) {
        return value ? 1 : 0;
    }

    private boolean compare(Integer currentValue, Condition condition) {
        Integer targetValue = condition.getValue();

        if (targetValue == null) {
            targetValue = 1;
        }

        return switch (condition.getOperation()) {
            case "EQUALS" -> currentValue.equals(targetValue);
            case "GREATER_THAN" -> currentValue > targetValue;
            case "LOWER_THAN" -> currentValue < targetValue;
            default -> false;
        };
    }
}