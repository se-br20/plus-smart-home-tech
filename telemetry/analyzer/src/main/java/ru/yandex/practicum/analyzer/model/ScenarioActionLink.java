package ru.yandex.practicum.analyzer.model;

import jakarta.persistence.*;

@Entity
@Table(name = "scenario_actions")
public class ScenarioActionLink {

    @EmbeddedId
    private ScenarioActionId id = new ScenarioActionId();

    @ManyToOne
    @MapsId("scenarioId")
    @JoinColumn(name = "scenario_id")
    private Scenario scenario;

    @ManyToOne
    @MapsId("sensorId")
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("actionId")
    @JoinColumn(name = "action_id")
    private Action action;

    public ScenarioActionId getId() {
        return id;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public Action getAction() {
        return action;
    }

    public void setId(ScenarioActionId id) {
        this.id = id;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
