package project.self_development.routine.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackRequest {

    @NotNull(message = "emotion은 필수입니다.")
    private Integer emotion;  // ✅ int → Integer 변경

    @NotNull(message = "intensity는 필수입니다.")
    private Integer intensity;  // 마찬가지로 Integer로 바꾸는 것이 안전
}
