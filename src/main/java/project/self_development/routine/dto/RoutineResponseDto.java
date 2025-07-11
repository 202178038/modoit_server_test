package project.self_development.routine.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import project.self_development.routine.domain.RoutineMenu;

import java.time.LocalTime;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineResponseDto {
    private int id;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime timeSlot;
    private String content;
    @Builder.Default
    private boolean isCompleted = false;

    public static RoutineResponseDto of(RoutineMenu menu, boolean isCompleted) {
        return RoutineResponseDto.builder()
                .id(menu.getId())
                .timeSlot(menu.getTimeSlot()) // RoutineMenu에 timeSlot이 있어야 함
                .content(menu.getContent())   // RoutineMenu에 content가 있어야 함
                .isCompleted(isCompleted)
                .build();
    }

}
