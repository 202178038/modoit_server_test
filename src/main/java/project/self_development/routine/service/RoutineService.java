package project.self_development.routine.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import project.self_development.routine.domain.RoutineDefinition;
import project.self_development.routine.domain.RoutineMenu;
import project.self_development.routine.domain.RoutineRecord;
import project.self_development.routine.domain.User;
import project.self_development.routine.dto.*;
import project.self_development.routine.repository.RoutineDefinitionRepository;
import project.self_development.routine.repository.RoutineMenuRepository;
import project.self_development.routine.repository.RoutineRecordRepository;
import project.self_development.routine.security.CustomUserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoutineService {
    private final RoutineMenuRepository routineMenuRepository;
    private final RoutineRecordRepository routineRecordRepository;
    private final RoutineDefinitionRepository routineDefinitionRepository;

    public List<RoutineResponseDto> getRoutine(LocalDate date){
        LocalDate now = LocalDate.now();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        List<RoutineMenu> menus = routineMenuRepository.findAllByUserAndIsActiveTrue(user);


        if(Objects.equals(date, now)){
            return menus.stream()
                    .map(menu -> {
                        Optional<RoutineRecord> recordOpt = routineRecordRepository
                                .findByRoutineMenuIdAndUserIdAndDate(menu.getId(), user.getId(), date);
                        boolean isCompleted = recordOpt.map(RoutineRecord::isCompleted).orElse(false);
                        return RoutineResponseDto.of(menu, isCompleted);
                    })
                    .collect(Collectors.toList());
        }
        Optional<List<RoutineRecord>> optionalRecords = routineRecordRepository.findAllByUserIdAndDate(user.getId(), date);

        // 저장된 기록이 없다면 빈 리스트 반환
        return optionalRecords.map(routineRecords -> routineRecords.stream()
                .map(record -> RoutineResponseDto.of(record.getRoutineMenu(), record.isCompleted()))
                .collect(Collectors.toList())).orElseGet(ArrayList::new);

    }
    public RoutineCompleteDto saveCompletedRoutine(int routineMenuId){
        LocalDate now = LocalDate.now();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        // 1. RoutineMenu가 현재 사용자 것인지 확인
        RoutineMenu menu = routineMenuRepository.findById(routineMenuId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 루틴 메뉴가 존재하지 않습니다."));

        if (menu.getUser().getId() != user.getId()) {
            throw new AccessDeniedException("다른 사용자의 루틴을 수정할 수 없습니다.");
        }

        // 2. 오늘 날짜 기준으로 RoutineRecord 조회
        Optional<RoutineRecord> recordOpt = routineRecordRepository.findByRoutineMenuIdAndUserIdAndDate(routineMenuId, user.getId(), now);

        RoutineRecord routineRecord;
        if (recordOpt.isEmpty()) {
            routineRecord = new RoutineRecord();
            routineRecord.setDate(now);
            routineRecord.setUser(user);
            routineRecord.setRoutineMenu(menu);
            routineRecord.setCompleted(true);
        } else {
            routineRecord = recordOpt.get();
            routineRecord.setCompleted(!routineRecord.isCompleted()); // toggle
        }

        RoutineRecord saved = routineRecordRepository.save(routineRecord);
        return new RoutineCompleteDto(routineMenuId, saved.isCompleted());
    }


    public void addRoutine(RoutineRequestDto dto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        RoutineDefinition routineDefinition = new RoutineDefinition();
        routineDefinition.setContent(dto.getContent());
        routineDefinition.setUser(user);
        routineDefinition.setTime_slot(dto.getTimeSlot());
        RoutineDefinition saved = routineDefinitionRepository.save(routineDefinition);
        RoutineMenu routineMenu = new RoutineMenu();
        routineMenu.setUser(user);
        routineMenu.setTimeSlot(dto.getTimeSlot());
        routineMenu.setContent(dto.getContent());
        routineMenu.setCustom(saved);
        routineMenuRepository.save(routineMenu);
    }
    public RoutineDetailResponseDto getRoutineDetail(int id){
        Optional<RoutineMenu> optional = routineMenuRepository.findById(id);
        RoutineMenu routineMenu = optional.orElseThrow(() -> new EntityNotFoundException("해당 루틴이 없습니다."));

        RoutineDetailResponseDto responseDto = new RoutineDetailResponseDto();
        responseDto.setId(routineMenu.getId());
        responseDto.setContent(routineMenu.getContent());
        responseDto.setTimeSlot(routineMenu.getTimeSlot());
        return responseDto;
    }
    public void editRoutineDetail(RoutineDetailRequestDto dto){
        if (dto == null) {
            throw new IllegalArgumentException("시간과 루틴을 제대로 입력해주세요.");
        }

        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        RoutineMenu routineMenu = routineMenuRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("기존 루틴을 찾지 못했습니다."));

        boolean isTimeChanged = !routineMenu.getTimeSlot().equals(dto.getTimeSlot());
        boolean isContentChanged = !Objects.equals(routineMenu.getContent(), dto.getContent());

        if (!(isTimeChanged || isContentChanged)) {
            return; // 변경 사항 없음
        }

        routineMenu.setActive(false);
        routineMenuRepository.save(routineMenu);

        RoutineDefinition newDefinition = new RoutineDefinition();
        newDefinition.setUser(user);
        newDefinition.setContent(dto.getContent());
        newDefinition.setTime_slot(dto.getTimeSlot());

        RoutineDefinition saved = routineDefinitionRepository.save(newDefinition);

        RoutineMenu newRoutineMenu = new RoutineMenu();
        newRoutineMenu.setUser(user);
        newRoutineMenu.setCustom(saved);
        newRoutineMenu.setContent(saved.getContent());
        newRoutineMenu.setTimeSlot(saved.getTime_slot());
        routineMenuRepository.save(newRoutineMenu);

    }
    public void saveAllRoutineByDate() {
        User user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        LocalDate now = LocalDate.now();

        List<RoutineMenu> activeMenus = routineMenuRepository.findAllByUserAndIsActiveTrue(user);

        for (RoutineMenu menu : activeMenus) {
            Optional<RoutineRecord> recordOpt =
                    routineRecordRepository.findByRoutineMenuIdAndUserIdAndDate(menu.getId(), user.getId(), now);

            if (recordOpt.isPresent()) {
                continue; // 중복 방지
            }

            RoutineRecord routineRecord = new RoutineRecord();
            routineRecord.setDate(now);
            routineRecord.setUser(user);
            routineRecord.setRoutineMenu(menu);
            routineRecord.setCompleted(false);
            routineRecordRepository.save(routineRecord);
        }
    }



}
