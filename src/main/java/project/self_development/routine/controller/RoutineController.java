package project.self_development.routine.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import project.self_development.routine.dto.*;
import project.self_development.routine.service.RoutineService;

import java.time.LocalDate;
import java.util.List;

@RestController
@AllArgsConstructor
    public class RoutineController {
        private final RoutineService routineService;

        @GetMapping("/main")
        public ResponseEntity<List<?>> getMain(@Valid @RequestParam(value = "date", required = false)
                                             @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate date){
            if(date == null){
                List<?> routine = routineService.getRoutine(LocalDate.now());
                return ResponseEntity.ok(routine);
            }
            List<?> routine = routineService.getRoutine(date);
            return ResponseEntity.ok(routine);
        }
        @PostMapping("/check-routine")
        public ResponseEntity<?> updateEditRoutine(@Valid @RequestBody RoutineCompleteRequestDto dto){
            System.out.println("/check 시작 & dto 값 = " + dto.getId());
            RoutineCompleteDto routineCompleteDto = routineService.saveCompletedRoutine(dto.getId());
            return ResponseEntity.ok(routineCompleteDto);
        }
        @PostMapping("/add-routine")
        public ResponseEntity<?> addRoutine(@Valid @RequestBody RoutineRequestDto dto){
            routineService.addRoutine(dto);
            return ResponseEntity.ok("루틴이 추가되었습니다.");
        }
        @GetMapping("/edit-routine-detail/{id}")
        public ResponseEntity<?> getEditRoutine(@Valid @PathVariable("id") int id) {
            RoutineDetailResponseDto routineDetail = routineService.getRoutineDetail(id);
            return ResponseEntity.ok(routineDetail);
        }
        @PatchMapping("/edit-routine-detail")
        public ResponseEntity<?> editRoutine(@Valid @RequestBody RoutineDetailRequestDto dto){
            routineService.editRoutineDetail(dto);
            return ResponseEntity.ok("수정 완료되었습니다.");
        }
        @PostMapping("/finish")
        public ResponseEntity<?> saveAllRoutine(){
            routineService.saveAllRoutineByDate();
            return ResponseEntity.ok("루틴 끝내기 성공했습니다.");
        }

    }
