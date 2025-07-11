package project.self_development.routine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import project.self_development.routine.domain.RoutineMenu;

@Getter
@AllArgsConstructor
// 루틴 목록 응답 (공통 사용)
public class UncompletedRoutineResponse {
    private int id;
    private String content;
    private String timeSlot; // "HH:mm" 형식

    public UncompletedRoutineResponse(RoutineMenu menu) {
        this.id = menu.getId();
        this.content = menu.getContent();
        this.timeSlot = menu.getTimeSlot().toString(); // LocalTime → String
    }
}
