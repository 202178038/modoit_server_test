package project.self_development.routine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.self_development.routine.domain.RoutineDefinition;

import java.util.Optional;

@Repository
public interface RoutineDefinitionRepository extends JpaRepository<RoutineDefinition, Integer> {
    Optional<RoutineDefinition> findById(int id);
}
