package project.self_development.routine.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import project.self_development.routine.domain.*;
import project.self_development.routine.dto.*;
import project.self_development.routine.repository.*;
import project.self_development.routine.security.CustomUserDetails;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackCardService {

    private final RoutineMenuRepository routineMenuRepository;
    private final RoutinePresetRepository routinePresetRepository;
    private final RoutineRecordRepository routineRecordRepository;
    private final StateRepository stateRepository;

    // ✅ 1. 달성률 반환 (특정 날짜 기준)
    public FeedbackAchievementResponse getAchievementRate(LocalDate date) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        int total     = routineRecordRepository.countByUserAndDate(user, date);
        int completed = routineRecordRepository.countByUserAndDateAndIsCompletedTrue(user, date);

        double rate = (total == 0) ? 0.0
                : Math.round(((double) completed / total * 100) * 10) / 10.0;

        return new FeedbackAchievementResponse(rate);
    }



    // ✅ 2. 오늘 감정/강도 피드백 저장 (있으면 덮어쓰기)
    public void saveFeedback(FeedbackRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        LocalDate today = LocalDate.now();

        // 오늘 기록이 있으면 가져오고, 없으면 새로 만든다
        State state = stateRepository.findByUserAndDate(user, today)
                .orElseGet(() -> State.builder()
                        .user(user)
                        .date(today)
                        .build());

        // 값 덮어쓰기
        state.setEmotion(request.getEmotion());
        state.setIntensity(request.getIntensity());

        // JPA save → 신규일 땐 INSERT, 기존일 땐 UPDATE
        stateRepository.save(state);
    }



    // ✅ 3. 오늘(당일) 루틴 중 아직 완료되지 않은 항목만 조회
    public List<UncompletedRoutineResponse> getUncompletedRoutines() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        LocalDate today = LocalDate.now();

    /* 오늘 날짜의 RoutineRecord 중 isCompleted = false 인 것만 걸러서
       해당 RoutineMenu를 응답 DTO로 변환 */
        return routineRecordRepository.findByUserAndDate(user, today).stream()
                .filter(record -> !record.isCompleted())                 // 미완료
                .map(record -> new UncompletedRoutineResponse(record.getRoutineMenu()))
                .collect(Collectors.toList());
    }

    // ✅ 4. 오늘 상태 기반 추천 루틴 4개 반환
    public List<RoutineRecommendResponse> getTodayRandomRecommendRoutines() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        LocalDate today = LocalDate.now();

        // 오늘 감정/강도 정보가 없다면 예외 발생
        State state = stateRepository.findByUserAndDate(user, today)
                .orElseThrow(() -> new EntityNotFoundException("오늘의 감정/강도 피드백이 존재하지 않습니다."));

        // 상태 기반으로 추천 루틴 조회
        List<RoutinePreset> matchedPresets =
                routinePresetRepository.findByEmotionAndDifficulty(state.getEmotion(), state.getIntensity());

        Collections.shuffle(matchedPresets); // 랜덤 추천
        return matchedPresets.stream()
                .limit(4)
                .map(RoutineRecommendResponse::new)
                .collect(Collectors.toList());
    }

    // ✅ 5. 추천 루틴 저장 (사용자 메뉴에 preset 중복 → skip)
    @Transactional
    public void saveRecommendedRoutines(List<RoutineRecommendSaveRequest> requests) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        Set<Integer> processed = new HashSet<>();

        for (RoutineRecommendSaveRequest req : requests) {
            // ① 한 요청 내 중복 id 제거
            if (!processed.add(req.getId())) continue;

            // ② preset 존재 여부 확인
            RoutinePreset preset = routinePresetRepository.findById(req.getId())
                    .orElseThrow(() ->
                            new EntityNotFoundException("존재하지 않는 presetId: " + req.getId()));

            // ③ 이미 같은 preset이 있는지 (활성/비활성 모두 조회)
            Optional<RoutineMenu> existingOpt =
                    routineMenuRepository.findByUserAndPreset(user, preset);

            if (existingOpt.isPresent()) {
                RoutineMenu existing = existingOpt.get();
                if (!existing.isActive()) {          // soft delete 상태면 재활성
                    existing.setActive(true);
                    routineMenuRepository.save(existing);
                }
                continue;                            // 이미 활성 상태면 skip
            }

            // ④ 새 INSERT
            RoutineMenu newMenu = RoutineMenu.builder()
                    .user(user)
                    .preset(preset)
                    .content(preset.getContent())
                    .timeSlot(preset.getTime_slot())
                    .isActive(true)
                    .build();

            routineMenuRepository.save(newMenu);
        }
    }



    // ✅ 6. 삭제 가능한 루틴 조회
    public List<RoutineDeleteResponse> getDeletableRoutines() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        return routineMenuRepository.findByUserAndIsActiveTrue(user).stream()
                .map(RoutineDeleteResponse::new)
                .collect(Collectors.toList());
    }

    // ✅ 7. 루틴 삭제 처리 (soft delete)
    public void deleteRoutines(List<RoutineDeleteRequest> requests) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        for (RoutineDeleteRequest req : requests) {
            RoutineMenu menu = routineMenuRepository.findById(req.getId())
                    .orElseThrow(() -> new EntityNotFoundException("루틴 ID가 존재하지 않습니다: " + req.getId()));

            if (menu.getUser().getId() != user.getId()) {
                throw new AccessDeniedException("삭제 권한이 없습니다. ID: " + req.getId());
            }

//            // 이미 삭제된 루틴인지 확인
//            if (!menu.isActive()) {
//                throw new IllegalStateException("이미 비활성화된 루틴입니다. ID: " + req.getId());
//            }

            // soft delete 처리
            menu.setActive(false);
            routineMenuRepository.save(menu);
        }
    }

    // ✅ 8. 특정 연도와 월의 피드백 카드 전체 조회 (소수점 1자리 달성률 포함)
    public List<FeedbackCardResponse> getFeedbackCards(int year, int month) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        List<State> states = stateRepository.findByUser(user);

        // 1) 파라미터 검증
        if (month < 1 || month > 12)
            throw new IllegalArgumentException("month는 1~12 사이여야 합니다.");

        return states.stream()
                .filter(state -> {
                    LocalDate date = state.getDate();
                    return date.getYear() == year && date.getMonthValue() == month;
                })

                .map(state -> {
                    LocalDate date = state.getDate();
                    int total = routineRecordRepository.countByUserAndDate(user, date);
                    int completed = routineRecordRepository.countByUserAndDateAndIsCompletedTrue(user, date);

                    // 소수점 1자리까지 반올림한 달성률 계산
                    double achievementRate = (total == 0) ? 0.0 : Math.round(((double) completed / total * 100) * 10) / 10.0;

                    return new FeedbackCardResponse(state, achievementRate);
                })
                .collect(Collectors.toList());
    }


    // ✅ 9. 감정 기준 추천 5개
    public List<RoutineRecommendResponse> getSampleEmotion(int emotion) {

        if (emotion < 1 || emotion > 3) {
            throw new IllegalArgumentException("emotion 값은 1~3 사이여야 합니다.");
        }

        List<RoutinePreset> list = routinePresetRepository.findByEmotion(emotion);
        Collections.shuffle(list);
        return list.stream()
                .limit(5)
                .map(RoutineRecommendResponse::new)
                .toList();
    }

    // ✅ 10.강도 기준 추천 5개
    public List<RoutineRecommendResponse> getSampleDifficulty(int difficulty) {

        if (difficulty < 1 || difficulty > 3) {
            throw new IllegalArgumentException("difficulty 값은 1~3 사이여야 합니다.");
        }

        List<RoutinePreset> list = routinePresetRepository.findByDifficulty(difficulty);
        Collections.shuffle(list);
        return list.stream()
                .limit(5)
                .map(RoutineRecommendResponse::new)
                .toList();
    }


    // ✅ 11. 샘플 루틴 저장 (중복 presetId → 건너뛰기)
    @Transactional
    public void saveSampleRoutines(List<RoutineRecommendSaveRequest> requests) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        Set<Integer> processed = new HashSet<>();

        for (RoutineRecommendSaveRequest req : requests) {
            // ① 한 요청 내 중복 id 제거
            if (!processed.add(req.getId())) continue;

            // ② preset 존재 여부 확인
            RoutinePreset preset = routinePresetRepository.findById(req.getId())
                    .orElseThrow(() ->
                            new EntityNotFoundException("존재하지 않는 presetId: " + req.getId()));

            // ③ 이미 같은 preset이 있는지 (활성/비활성 모두 조회)
            Optional<RoutineMenu> existingOpt =
                    routineMenuRepository.findByUserAndPreset(user, preset);

            if (existingOpt.isPresent()) {
                RoutineMenu existing = existingOpt.get();
                if (!existing.isActive()) {          // soft delete 상태면 재활성
                    existing.setActive(true);
                    routineMenuRepository.save(existing);
                }
                continue;                            // 이미 활성 상태면 skip
            }

            // ④ 새 INSERT
            RoutineMenu newMenu = RoutineMenu.builder()
                    .user(user)
                    .preset(preset)
                    .content(preset.getContent())
                    .timeSlot(preset.getTime_slot())
                    .isActive(true)
                    .build();

            routineMenuRepository.save(newMenu);
        }
    }

}
