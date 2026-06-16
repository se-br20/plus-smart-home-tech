package ru.yandex.practicum.analyzer.model;

import jakarta.persistence.*;

@Entity
@Table(name = "actions")
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private Integer value;

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Integer getValue() {
        return value;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}