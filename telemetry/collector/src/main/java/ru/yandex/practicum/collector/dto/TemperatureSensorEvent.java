package ru.yandex.practicum.collector.dto;

import jakarta.validation.constraints.NotNull;

public class TemperatureSensorEvent extends SensorEvent {
    @NotNull
    private Integer temperatureC;

    @NotNull
    private Integer temperatureF;

    public Integer getTemperatureC() {
        return temperatureC;
    }

    public Integer getTemperatureF() {
        return temperatureF;
    }

    public void setTemperatureC(Integer temperatureC) {
        this.temperatureC = temperatureC;
    }

    public void setTemperatureF(Integer temperatureF) {
        this.temperatureF = temperatureF;
    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}
