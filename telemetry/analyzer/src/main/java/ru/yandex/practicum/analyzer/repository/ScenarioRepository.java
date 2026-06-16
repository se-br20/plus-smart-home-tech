package ru.yandex.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.analyzer.model.Scenario;

import java.util.List;
import java.util.Optional;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {

    List<Scenario> findAllByHubId(String hubId);

    Optional<Scenario> findByHubIdAndName(String hubId, String name);

    @Query("""
            select distinct s
            from Scenario s
            left join s.conditions c
            left join s.actions a
            where s.hubId = :hubId
              and (
                    c.sensor.id = :sensorId
                    or
                    a.sensor.id = :sensorId
                  )
            """)
    List<Scenario> findAllByHubIdAndSensorId(@Param("hubId") String hubId,
                                             @Param("sensorId") String sensorId);
}