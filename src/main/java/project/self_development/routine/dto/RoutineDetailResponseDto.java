package project.self_development.routine.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Data
@Setter
public class RoutineDetailResponseDto {
    private int id;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime timeSlot;
    private String content;
}
