package ru.yandex.practicum.analyzer.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ScenarioConditionId implements Serializable {

    private Long scenarioId;
    private String sensorId;
    private Long conditionId;

    public Long getScenarioId() {
        return scenarioId;
    }

    public String getSensorId() {
        return sensorId;
    }

    public Long getConditionId() {
        return conditionId;
    }

    public void setScenarioId(Long scenarioId) {
        this.scenarioId = scenarioId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public void setConditionId(Long conditionId) {
        this.conditionId = conditionId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ScenarioConditionId that)) {
            return false;
        }
        return Objects.equals(scenarioId, that.scenarioId)
                && Objects.equals(sensorId, that.sensorId)
                && Objects.equals(conditionId, that.conditionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scenarioId, sensorId, conditionId);
    }
}