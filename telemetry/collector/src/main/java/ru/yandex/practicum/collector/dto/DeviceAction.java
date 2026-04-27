package ru.yandex.practicum.collector.dto;

public class DeviceAction {
    private String sensorId;
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
