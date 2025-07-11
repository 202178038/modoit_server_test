package project.self_development.routine.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import project.self_development.routine.domain.State;
import project.self_development.routine.domain.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<State, Integer> {
    boolean existsByUserAndDate(User user, LocalDate date);

    List<State> findByUser(User user);

    Optional<State> findByUserAndDate(User user, LocalDate date);

}
