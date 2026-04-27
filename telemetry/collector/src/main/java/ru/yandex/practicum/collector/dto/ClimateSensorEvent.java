package ru.yandex.practicum.collector.dto;

import jakarta.validation.constraints.NotNull;

public class ClimateSensorEvent extends SensorEvent {
    @NotNull
    private Integer temperatureC;

    @NotNull
    private Integer humidity;

    @NotNull
    private Integer co2Level;

    public Integer getTemperatureC() {
        return temperatureC;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public Integer getCo2Level() {
        return co2Level;
    }

    public void setTemperatureC(Integer temperatureC) {
        this.temperatureC = temperatureC;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public void setCo2Level(Integer co2Level) {
        this.co2Level = co2Level;
    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}