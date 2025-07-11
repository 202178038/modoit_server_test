package project.self_development.routine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.self_development.routine.domain.RoutinePreset;

import java.util.List;

@Repository
public interface RoutinePresetRepository extends JpaRepository<RoutinePreset, Integer> {
    List<RoutinePreset> findByEmotionAndDifficultyAndCategory(int emotion, int difficulty, int category);
    // 감정(emotion)과 강도(intensity)에 따라 추천 루틴 조회
    List<RoutinePreset> findByEmotionAndDifficulty(int emotion, int difficulty);

    List<RoutinePreset> findByEmotion(int emotion);

    List<RoutinePreset> findByDifficulty(int difficulty);

}
