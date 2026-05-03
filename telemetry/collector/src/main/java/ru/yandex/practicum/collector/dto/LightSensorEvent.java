package ru.yandex.practicum.collector.dto;

public class LightSensorEvent extends SensorEvent {
    private Integer linkQuality;
    private Integer luminosity;

    public Integer getLinkQuality() {
        return linkQuality;
    }

    public Integer getLuminosity() {
        return luminosity;
    }

    public void setLinkQuality(Integer linkQuality) {
        this.linkQuality = linkQuality;
    }

    public void setLuminosity(Integer luminosity) {
        this.luminosity = luminosity;
    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}