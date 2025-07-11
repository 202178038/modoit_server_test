package project.self_development.routine.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@NoArgsConstructor
public class RoutineCompleteRequestDto {
    @NotNull(message = "id를 입력해주세요.")
    private int id;
}

