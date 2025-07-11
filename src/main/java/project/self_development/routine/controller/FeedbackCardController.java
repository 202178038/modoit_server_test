package project.self_development.routine.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.self_development.routine.domain.User;
import project.self_development.routine.dto.*;
import project.self_development.routine.service.FeedbackCardService;

import static project.self_development.routine.security.SessionUserUtil.getSessionUser;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated // ✅ 컨트롤러 수준에서 @Valid 및 @NotEmpty 등의 유효성 검사를 활성화
public class FeedbackCardController {

    private final FeedbackCardService feedbackCardService;

    // ✅ 1. 오늘의 루틴 달성률 조회 (퍼센트로 반환)
    @GetMapping("/feedback-card")
    public ResponseEntity<FeedbackAchievementResponse> getAchievementRate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(feedbackCardService.getAchievementRate(date));
    }
//LocalDate 는 기본적으로 ISO-8601(yyyy-MM-dd) 문자열을 자동 파싱합니다. 하지만
//전역 설정(spring.mvc.format.date)을 바꿨거나
//라이브러리가 2025/07/01 같은 다른 형식을 보내오는 경우엔 실패할 수 있습니다.
//@DateTimeFormat(iso = ISO.DATE) 를 달아 두면 무조건 yyyy-MM-dd 로만 해석하겠다는 의도를 코드에 남길 수 있습니다.

    // ✅ 2. 오늘 감정/강도 피드백 저장 (하루 한 번만 가능)
    @PostMapping("/feedback-card")
    public ResponseEntity<String> saveFeedback(@Valid @RequestBody FeedbackRequest request) {
        feedbackCardService.saveFeedback(request); // 이미 등록된 경우 예외 발생
        return ResponseEntity.ok("오늘의 감정/강도 피드백이 저장되었습니다."); // 성공 시 응답 본문 없이 200 OK
    }

    // ✅ 3. 오늘 완료하지 않은 루틴 조회 (RoutineRecord 기준 미완료 루틴)
    @GetMapping("/delect-routine")
    public ResponseEntity<List<UncompletedRoutineResponse>> getUncompletedRoutines() {
        return ResponseEntity.ok(feedbackCardService.getUncompletedRoutines()); // 아직 수행 안 한 루틴 목록 반환
    }

    // ✅ 4. 오늘 상태(emotion, intensity) 기반 루틴 추천 (4개 무작위 반환)
    @GetMapping("/recommend-routine")
    public ResponseEntity<List<RoutineRecommendResponse>> getTodayRecommendRoutines() {
        return ResponseEntity.ok(feedbackCardService.getTodayRandomRecommendRoutines());
    }

    // ✅ 5. 추천 루틴을 내 루틴으로 저장
    @PostMapping("/recommend-routine")
    public ResponseEntity<String> saveRecommendedRoutines(
            @RequestBody @NotEmpty(message = "추천 루틴 리스트는 비어 있을 수 없습니다.") // 요청 리스트가 비어 있으면 400 Bad Request
            List<@Valid RoutineRecommendSaveRequest> requests
    ) {
        feedbackCardService.saveRecommendedRoutines(requests); // 중복 presetId, 이미 저장된 루틴 처리 포함
        return ResponseEntity.ok("추천 루틴이 저장되었습니다.");
    }

    // ✅ 6. 삭제 가능한 루틴 목록 조회 (현재 isActive = true인 루틴만 반환)
    @GetMapping("/edit-routine")
    public ResponseEntity<List<RoutineDeleteResponse>> getDeletableRoutines() {
        return ResponseEntity.ok(feedbackCardService.getDeletableRoutines());
    }

    // ✅ 7. 루틴 삭제 요청 처리 (soft delete 방식: isActive = false로 변경)
    @PostMapping("/edit-routine")
    public ResponseEntity<String> deleteRoutines(
            @RequestBody @NotEmpty(message = "삭제할 루틴 리스트는 비어 있을 수 없습니다.")
            List<RoutineDeleteRequest> requests
    ) {
        feedbackCardService.deleteRoutines(requests); // 본인 소유 여부 및 삭제 여부 확인 포함
        return ResponseEntity.ok("루틴이 삭제되었습니다.");
    }

    // ✅ 8. 날짜별 피드백 카드 전체 조회 (State + 루틴 달성률 포함)
    @GetMapping("/check-feedback-card")
    public ResponseEntity<List<FeedbackCardResponse>> getFeedbackCards(
            @RequestParam("year") int year,
            @RequestParam("month") int month)
    {
        return ResponseEntity.ok(feedbackCardService.getFeedbackCards(year,month)); // 날짜별 감정/강도 + 퍼센트 반환
    }

    // ✅ 9. 감정(emotion) 전용 5개
    @GetMapping("/sample-routine/emotion")
    public ResponseEntity<List<RoutineRecommendResponse>> byEmotion(
            @RequestParam(value = "value") int emotion) {          // value=1,2,3
        return ResponseEntity.ok(feedbackCardService.getSampleEmotion(emotion));
    }

    // ✅ 10. 강도(difficulty) 전용 5개
    @GetMapping("/sample-routine/difficulty")
    public ResponseEntity<List<RoutineRecommendResponse>> byDifficulty(
            @RequestParam(value = "value") int difficulty) {       // value=1,2,3
        return ResponseEntity.ok(feedbackCardService.getSampleDifficulty(difficulty));
    }

    // ✅ 11. 샘플 루틴 저장 (중복 presetId 및 기존 루틴 등록 여부 검사 포함)
    @PostMapping("/sample-routine")
    public ResponseEntity<String> saveSampleRoutines(
            @RequestBody @NotEmpty(message = "샘플 루틴 리스트는 비어 있을 수 없습니다.")
            List<RoutineRecommendSaveRequest> requests
    ) {
        feedbackCardService.saveSampleRoutines(requests); // 로직은 추천 루틴 저장과 동일
        return ResponseEntity.ok("샘플 루틴이 저장되었습니다.");
    }
}
