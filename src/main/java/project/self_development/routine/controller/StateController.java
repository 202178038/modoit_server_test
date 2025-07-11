package project.self_development.routine.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import project.self_development.routine.dto.StateRequestDto;
import project.self_development.routine.security.CustomUserDetails;
import project.self_development.routine.service.StateService;

@Controller
@AllArgsConstructor
public class StateController {
    private final StateService stateService;

    @PostMapping("/pre-research")
    public ResponseEntity<?> preResearch(@Valid @RequestBody StateRequestDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Integer userId = userDetails.getUser().getId();
        System.out.println("userId : " + userId);
        System.out.println("Emotion : " + dto.getEmotion());
        System.out.println("Intensity : " + dto.getIntensity());

        stateService.AnalyzeEmotions(userId, dto);

        return ResponseEntity.ok("감정 분석 및 상태 저장이 완료되었습니다.");
    }

}
