package project.self_development.routine.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import project.self_development.routine.domain.RoutineMenu;

@Getter
@AllArgsConstructor
// 루틴 목록 응답 (공통 사용)
public class RoutineDeleteResponse {
    private int id;
    private String content;
    @JsonFormat(pattern = "HH:mm")
    private String timeSlot; // "HH:mm" 형식

    public RoutineDeleteResponse (RoutineMenu menu) {
        this.id = menu.getId();
        this.content = menu.getContent();
        this.timeSlot = menu.getTimeSlot().toString(); // LocalTime → String
    }
}
