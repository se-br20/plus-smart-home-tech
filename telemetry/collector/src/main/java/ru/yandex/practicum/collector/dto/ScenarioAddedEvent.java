package ru.yandex.practicum.collector.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class ScenarioAddedEvent extends HubEvent {
    @Size(min = 3)
    private String name;

    @NotEmpty
    private List<ScenarioCondition> conditions;

    @NotEmpty
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