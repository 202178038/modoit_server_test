package project.self_development.routine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import project.self_development.routine.domain.RoutinePreset;

@Data
@AllArgsConstructor
// 추천 루틴 응답 DTO
public class RoutineRecommendResponse {
    private int id;        // 추천 루틴 ID (RoutinePreset ID)
    private String content; // 루틴 내용 (예: 감사 일기)

    // RoutinePreset을 받아 처리하는 생성자 추가
    public RoutineRecommendResponse(RoutinePreset preset) {
        this.id = preset.getId();
        this.content = preset.getContent();
    }
}
