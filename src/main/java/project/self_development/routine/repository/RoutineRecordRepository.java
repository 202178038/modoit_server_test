package project.self_development.routine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.self_development.routine.domain.RoutineRecord;
import project.self_development.routine.domain.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoutineRecordRepository extends JpaRepository<RoutineRecord, Integer> {
    Optional<RoutineRecord> findById(int id);
    Optional<RoutineRecord> findByRoutineMenuId(int routineMenuId);
    Optional<RoutineRecord> findByRoutineMenuIdAndUserIdAndDate(int routineMenuId, int userId, LocalDate date);


    boolean existsByRoutineMenuIdAndDate(int routineMenuId, LocalDate date);

    Optional<List<RoutineRecord>> findAllByUserIdAndDate(int id, LocalDate date);

    // 특정 사용자가 특정 날짜에 수행한 전체 루틴 수 조회
    int countByUserAndDate(User user, LocalDate date);

    // 특정 사용자가 특정 날짜에 완료한 루틴 수 조회
    int countByUserAndDateAndIsCompletedTrue(User user, LocalDate date);

    // 특정 사용자가 특정 날짜에 수행한 루틴 기록 전체 조회
    List<RoutineRecord> findByUserAndDate(User user, LocalDate date);

}
