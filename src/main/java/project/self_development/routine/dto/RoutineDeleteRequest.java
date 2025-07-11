package project.self_development.routine.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
//루틴 삭제 요청 DTO
public class RoutineDeleteRequest {
    private int id;         // RoutineMenu ID (삭제 대상 루틴 ID)
//    private String content; // 루틴 내용
//    private String timeSlot; // LocalTime이 아닌 String으로 받아서 Service에서 변환  // 루틴 시간 (HH:mm 형식 문자열)
}
