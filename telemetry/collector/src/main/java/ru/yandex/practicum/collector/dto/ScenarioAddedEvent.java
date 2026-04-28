package ru.yandex.practicum.collector.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class ScenarioAddedEvent extends HubEvent {
    @NotBlank
    @Size(min = 3)
    private String name;

    @NotEmpty
    @Valid
    private List<ScenarioCondition> conditions;

    @NotEmpty
    @Valid
    private List<DeviceAction> actions;

    public String getName() {
        return name;
    }

    public List<ScenarioCondition> getConditions() {
        return conditions;
    }

    public List<DeviceAction> getActions() {
        return actions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setConditions(List<ScenarioCondition> conditions) {
        this.conditions = conditions;
    }

    public void setActions(List<DeviceAction> actions) {
        this.actions = actions;
    }

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}