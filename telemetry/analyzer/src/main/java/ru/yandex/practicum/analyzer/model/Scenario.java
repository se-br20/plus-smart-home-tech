package ru.yandex.practicum.analyzer.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "scenarios", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"hub_id", "name"})
})
public class Scenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hub_id")
    private String hubId;

    private String name;

    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ScenarioConditionLink> conditions = new ArrayList<>();

    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ScenarioActionLink> actions = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getHubId() {
        return hubId;
    }

    public String getName() {
        return name;
    }

    public List<ScenarioConditionLink> getConditions() {
        return conditions;
    }

    public List<ScenarioActionLink> getActions() {
        return actions;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setHubId(String hubId) {
        this.hubId = hubId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setConditions(List<ScenarioConditionLink> conditions) {
        this.conditions = conditions;
    }

    public void setActions(List<ScenarioActionLink> actions) {
        this.actions = actions;
    }
}