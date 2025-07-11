package project.self_development.routine.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Data
@Setter
public class RoutineDetailRequestDto {
    private int id;
    @DateTimeFormat(pattern = "HH:mm")
    @NotNull(message = "시간을 입력해주세요.")
    private LocalTime timeSlot;
    @NotBlank(message = "루틴 내용을 입력해주세요.")
    private String content;

}
