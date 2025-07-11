package project.self_development.routine.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import project.self_development.routine.domain.State;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class FeedbackCardResponse {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private int emotion;
    private int intensity;
    private double achievementRate;

    public FeedbackCardResponse(State state, double achievementRate) {
        this.date = state.getDate();
        this.emotion = state.getEmotion();
        this.intensity = state.getIntensity();
        this.achievementRate = achievementRate;
    }
}
