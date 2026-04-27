package ru.yandex.practicum.collector.dto;

import jakarta.validation.constraints.NotNull;

public class MotionSensorEvent extends SensorEvent {
    @NotNull
    private Integer linkQuality;

    @NotNull
    private Boolean motion;

    @NotNull
    private Integer voltage;

    public Integer getLinkQuality() {
        return linkQuality;
    }

    public Boolean getMotion() {
        return motion;
    }

    public Integer getVoltage() {
        return voltage;
    }

    public void setLinkQuality(Integer linkQuality) {
        this.linkQuality = linkQuality;
    }

    public void setMotion(Boolean motion) {
        this.motion = motion;
    }

    public void setVoltage(Integer voltage) {
        this.voltage = voltage;
    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
