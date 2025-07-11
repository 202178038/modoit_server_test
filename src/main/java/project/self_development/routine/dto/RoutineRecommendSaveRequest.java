package project.self_development.routine.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 추천 루틴 저장 요청 DTO
public class RoutineRecommendSaveRequest {
//    @NotNull(message = "preset id는 필수입니다.")
////    @Min(value = 1, message = "preset id는 1 이상이어야 합니다.")
    private int id;         // presetId
}
