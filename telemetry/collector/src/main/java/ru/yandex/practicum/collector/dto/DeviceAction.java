package ru.yandex.practicum.collector.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DeviceAction {
    @NotBlank
    private String sensorId;

    @NotNull
    private ActionType type;

    private Integer value;

    public String getSensorId() {
        return sensorId;
    }

    public ActionType getType() {
        return type;
    }

    public Integer getValue() {
        return value;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public void setType(ActionType type) {
        this.type = type;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
