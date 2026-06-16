package ru.yandex.practicum.analyzer.model;

import jakarta.persistence.*;

@Entity
@Table(name = "scenario_conditions")
public class ScenarioConditionLink {

    @EmbeddedId
    private ScenarioConditionId id = new ScenarioConditionId();

    @ManyToOne
    @MapsId("scenarioId")
    @JoinColumn(name = "scenario_id")
    private Scenario scenario;

    @ManyToOne
    @MapsId("sensorId")
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("conditionId")
    @JoinColumn(name = "condition_id")
    private Condition condition;

    public ScenarioConditionId getId() {
        return id;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setId(ScenarioConditionId id) {
        this.id = id;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }
}
