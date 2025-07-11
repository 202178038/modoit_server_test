package project.self_development.routine.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import project.self_development.routine.domain.RoutinePreset;
import project.self_development.routine.service.RoutinePresetService;

import java.util.List;

@Controller
@AllArgsConstructor
public class RecommendationController {
    private final RoutinePresetService routinePresetService;

    @GetMapping("/recommendation")
    public ResponseEntity<List<List<RoutinePreset>>> getRecommendations(@Valid @RequestParam int emotion,
                                                                        @RequestParam int intensity,
                                                                        @RequestParam int category){
        List<List<RoutinePreset>> recommendations =
                routinePresetService.getRecommendation(emotion, intensity, category);

        return ResponseEntity.ok(recommendations);
    }
    @PostMapping("/start")
    public ResponseEntity<?> saveRecommendation(@Valid @RequestBody List<RoutinePreset> routinePresetList){
        routinePresetService.saveRecommendation(routinePresetList);
        return ResponseEntity.ok("루틴 추천을 추가하였습니다.");
    }
}
