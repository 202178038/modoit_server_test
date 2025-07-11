package project.self_development.routine.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import project.self_development.routine.domain.RoutineMenu;
import project.self_development.routine.domain.RoutinePreset;
import project.self_development.routine.domain.User;
import project.self_development.routine.repository.RoutineMenuRepository;
import project.self_development.routine.repository.RoutinePresetRepository;
import project.self_development.routine.repository.UserRepository;
import project.self_development.routine.security.CustomUserDetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RoutinePresetService {
    private final RoutinePresetRepository routinePresetRepository;
    private final RoutineMenuRepository routineMenuRepository;
    private final UserRepository userRepository;
    public List<List<RoutinePreset>> getRecommendation(int emotion, int intensity, int category){
        List<RoutinePreset> allMatching = routinePresetRepository.findByEmotionAndDifficultyAndCategory(emotion, intensity, category);

        Collections.shuffle(allMatching);

        // 최대 15개까지 제한
        int resultSize = Math.min(15, allMatching.size());
        List<RoutinePreset> selected = allMatching.subList(0, resultSize);

        // 5개씩 잘라서 리스트로 묶기
        List<List<RoutinePreset>> result = new ArrayList<>();
        for(int i = 0; i < selected.size(); i+= 5){
            result.add(selected.subList(i, Math.min(i+5, selected.size())));
        }
        return result;
    }
    public void saveRecommendation(List<RoutinePreset> list){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        for (RoutinePreset routinePreset : list) {
            RoutineMenu routineMenu = new RoutineMenu();
            routineMenu.setUser(user);
            routineMenu.setPreset(routinePreset); // 객체 자체 전달
            routineMenu.setContent(routinePreset.getContent());
            routineMenu.setTimeSlot(routinePreset.getTime_slot());
            routineMenuRepository.save(routineMenu);
        }
    }


}
