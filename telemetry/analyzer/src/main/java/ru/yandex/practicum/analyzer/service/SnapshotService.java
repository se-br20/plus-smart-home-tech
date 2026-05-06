package ru.yandex.practicum.analyzer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.analyzer.model.Scenario;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.List;

@Service
public class SnapshotService {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioEvaluator scenarioEvaluator;
    private final ActionExecutor actionExecutor;

    public SnapshotService(ScenarioRepository scenarioRepository,
                           ScenarioEvaluator scenarioEvaluator,
                           ActionExecutor actionExecutor) {
        this.scenarioRepository = scenarioRepository;
        this.scenarioEvaluator = scenarioEvaluator;
        this.actionExecutor = actionExecutor;
    }

    @Transactional(readOnly = true)
    public void handleSnapshot(SensorsSnapshotAvro snapshot) {
        List<Scenario> scenarios = scenarioRepository.findAllByHubId(snapshot.getHubId());

        scenarios.stream()
                .filter(scenario -> scenarioEvaluator.matches(scenario, snapshot))
                .forEach(actionExecutor::execute);
    }
}