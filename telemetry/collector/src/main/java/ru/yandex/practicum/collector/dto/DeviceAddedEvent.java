package ru.yandex.practicum.collector.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DeviceAddedEvent extends HubEvent {
    @NotBlank
    private String id;

    @NotNull
    private DeviceType deviceType;

    public String getId() {
        return id;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}
