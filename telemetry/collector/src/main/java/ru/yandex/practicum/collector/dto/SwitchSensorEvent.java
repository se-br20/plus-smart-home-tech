package ru.yandex.practicum.collector.dto;

import jakarta.validation.constraints.NotNull;

public class SwitchSensorEvent extends SensorEvent {
    @NotNull
    private Boolean state;

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }
}
