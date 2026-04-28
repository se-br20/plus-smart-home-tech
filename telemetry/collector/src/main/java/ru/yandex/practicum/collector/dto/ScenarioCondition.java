package ru.yandex.practicum.collector.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ScenarioCondition {
    @NotBlank
    private String sensorId;

    @NotNull
    private ConditionType type;

    @NotNull
    private ConditionOperation operation;

    private Object value;

    public String getSensorId() {
        return sensorId;
    }

    public ConditionType getType() {
        return type;
    }

    public ConditionOperation getOperation() {
        return operation;
    }

    public Object getValue() {
        return value;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public void setType(ConditionType type) {
        this.type = type;
    }

    public void setOperation(ConditionOperation operation) {
        this.operation = operation;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}