package project.self_development.routine.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import project.self_development.routine.domain.RoutineMenu;
import project.self_development.routine.domain.User;
import project.self_development.routine.domain.RoutinePreset;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoutineMenuRepository extends JpaRepository<RoutineMenu, Integer> {
    List<RoutineMenu> findAllByUserAndIsActiveTrue(User user);
    Optional<RoutineMenu> findById(int id);
    // 진행 중인 루틴 목록 조회 (isActive = true)
    List<RoutineMenu> findByUserAndIsActiveTrue(User user);

    // 특정 유저의 루틴 ID로 조회
    Optional<RoutineMenu> findByIdAndUser(int id, User user);

    boolean existsByUserAndPreset(User user, RoutinePreset preset);

    Optional<RoutineMenu> findByUserAndPreset(User user, RoutinePreset preset);
}
