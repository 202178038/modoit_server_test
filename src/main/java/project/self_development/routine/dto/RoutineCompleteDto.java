package project.self_development.routine.dto;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class RoutineCompleteDto {
    private int id;
    private boolean isCompleted;

    public RoutineCompleteDto(int id, boolean completed) {
        this.id = id;
        this.isCompleted = completed;
    }
}
