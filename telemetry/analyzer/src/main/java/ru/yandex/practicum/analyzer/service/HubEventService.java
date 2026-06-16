package ru.yandex.practicum.analyzer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.analyzer.model.*;
import ru.yandex.practicum.analyzer.repository.ActionRepository;
import ru.yandex.practicum.analyzer.repository.ConditionRepository;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.*;

@Service
public class HubEventService {

    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;

    public HubEventService(SensorRepository sensorRepository,
                           ScenarioRepository scenarioRepository,
                           ConditionRepository conditionRepository,
                           ActionRepository actionRepository) {
        this.sensorRepository = sensorRepository;
        this.scenarioRepository = scenarioRepository;
        this.conditionRepository = conditionRepository;
        this.actionRepository = actionRepository;
    }

    @Transactional
    public void handle(HubEventAvro event) {
        Object payload = event.getPayload();

        if (payload instanceof DeviceAddedEventAvro deviceAdded) {
            sensorRepository.findByIdAndHubId(deviceAdded.getId(), event.getHubId())
                    .orElseGet(() -> sensorRepository.save(new Sensor(deviceAdded.getId(), event.getHubId())));
            return;
        }

        if (payload instanceof DeviceRemovedEventAvro deviceRemoved) {
            sensorRepository.findByIdAndHubId(deviceRemoved.getId(), event.getHubId())
                    .ifPresent(sensor -> {
                        List<Scenario> scenariosToDelete =
                                scenarioRepository.findAllByHubIdAndSensorId(event.getHubId(), sensor.getId());

                        scenarioRepository.deleteAll(scenariosToDelete);
                        sensorRepository.delete(sensor);
                    });
            return;
        }

        if (payload instanceof ScenarioRemovedEventAvro scenarioRemoved) {
            scenarioRepository.findByHubIdAndName(event.getHubId(), scenarioRemoved.getName())
                    .ifPresent(scenarioRepository::delete);
            return;
        }

        if (payload instanceof ScenarioAddedEventAvro scenarioAdded) {
            Scenario scenario = scenarioRepository.findByHubIdAndName(event.getHubId(), scenarioAdded.getName())
                    .orElseGet(Scenario::new);

            scenario.setHubId(event.getHubId());
            scenario.setName(scenarioAdded.getName());

            scenario.getConditions().clear();
            scenario.getActions().clear();

            Scenario savedScenario = scenarioRepository.saveAndFlush(scenario);

            Map<String, Sensor> sensorsById = loadOrCreateSensors(event.getHubId(), scenarioAdded);

            List<Condition> conditionsToSave = new ArrayList<>();

            for (ScenarioConditionAvro conditionAvro : scenarioAdded.getConditions()) {
                Condition condition = new Condition();
                condition.setType(conditionAvro.getType().name());
                condition.setOperation(conditionAvro.getOperation().name());
                condition.setValue(toInteger(conditionAvro.getValue()));

                conditionsToSave.add(condition);
            }

            List<Condition> savedConditions = conditionRepository.saveAll(conditionsToSave);

            for (int i = 0; i < scenarioAdded.getConditions().size(); i++) {
                ScenarioConditionAvro conditionAvro = scenarioAdded.getConditions().get(i);

                Sensor sensor = sensorsById.get(conditionAvro.getSensorId());

                Condition savedCondition = savedConditions.get(i);

                ScenarioConditionId id = new ScenarioConditionId();
                id.setScenarioId(savedScenario.getId());
                id.setSensorId(sensor.getId());
                id.setConditionId(savedCondition.getId());

                ScenarioConditionLink link = new ScenarioConditionLink();
                link.setId(id);
                link.setScenario(savedScenario);
                link.setSensor(sensor);
                link.setCondition(savedCondition);

                savedScenario.getConditions().add(link);
            }

            List<Action> actionsToSave = new ArrayList<>();

            for (DeviceActionAvro actionAvro : scenarioAdded.getActions()) {
                Action action = new Action();
                action.setType(actionAvro.getType().name());
                action.setValue(actionAvro.getValue());

                actionsToSave.add(action);
            }

            List<Action> savedActions = actionRepository.saveAll(actionsToSave);

            for (int i = 0; i < scenarioAdded.getActions().size(); i++) {
                DeviceActionAvro actionAvro = scenarioAdded.getActions().get(i);

                Sensor sensor = sensorsById.get(actionAvro.getSensorId());

                Action savedAction = savedActions.get(i);

                ScenarioActionId id = new ScenarioActionId();
                id.setScenarioId(savedScenario.getId());
                id.setSensorId(sensor.getId());
                id.setActionId(savedAction.getId());

                ScenarioActionLink link = new ScenarioActionLink();
                link.setId(id);
                link.setScenario(savedScenario);
                link.setSensor(sensor);
                link.setAction(savedAction);

                savedScenario.getActions().add(link);
            }

            scenarioRepository.save(savedScenario);
        }
    }

    private Map<String, Sensor> loadOrCreateSensors(String hubId, ScenarioAddedEventAvro scenarioAdded) {
        Set<String> sensorIds = new HashSet<>();

        scenarioAdded.getConditions()
                .forEach(condition -> sensorIds.add(condition.getSensorId()));

        scenarioAdded.getActions()
                .forEach(action -> sensorIds.add(action.getSensorId()));

        List<Sensor> existingSensors = sensorRepository.findAllByHubIdAndIdIn(hubId, sensorIds);

        Map<String, Sensor> sensorsById = new HashMap<>();

        existingSensors.forEach(sensor -> sensorsById.put(sensor.getId(), sensor));

        List<Sensor> sensorsToCreate = sensorIds.stream()
                .filter(sensorId -> !sensorsById.containsKey(sensorId))
                .map(sensorId -> new Sensor(sensorId, hubId))
                .toList();

        sensorRepository.saveAll(sensorsToCreate)
                .forEach(sensor -> sensorsById.put(sensor.getId(), sensor));

        return sensorsById;
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Boolean bool) {
            return bool ? 1 : 0;
        }

        if (value instanceof Integer integer) {
            return integer;
        }

        throw new IllegalArgumentException("Unsupported condition value: " + value);
    }
}