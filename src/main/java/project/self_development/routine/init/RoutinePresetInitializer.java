package project.self_development.routine.init;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import project.self_development.routine.domain.RoutinePreset;
import project.self_development.routine.repository.RoutinePresetRepository;

import java.io.InputStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoutinePresetInitializer implements ApplicationRunner {

    private final RoutinePresetRepository routinePresetRepository;
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("[HH:mm][:ss]");

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (routinePresetRepository.count() > 0) {
            log.info("✔️ routine_preset 테이블이 이미 채워져 있어 초기화를 건너뜁니다.");
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = new ClassPathResource("자기계발.json").getInputStream()) {
            JsonNode rootNode = objectMapper.readTree(inputStream);

            List<RoutinePreset> presets = new ArrayList<>();

            for (JsonNode node : rootNode) {
                try {
                    int emotion = node.path("emotion").asInt(1);
                    int difficulty = node.path("difficulty").asInt(1);
                    int category = node.path("category").asInt(1);
                    String content = node.path("content").asText("내용없음");
                    String timeSlotStr = node.path("time_slot").asText("00:00");

                    LocalTime timeSlot = LocalTime.parse(timeSlotStr, DateTimeFormatter.ofPattern("HH:mm"));

                    RoutinePreset preset = RoutinePreset.builder()
                            .emotion(emotion)
                            .difficulty(difficulty)
                            .category(category)
                            .content(content)
                            .time_slot(timeSlot)
                            .build();

                    presets.add(preset);
                } catch (Exception e) {
                    log.warn("⚠️ JSON 항목 파싱 오류: {}", node.toString(), e);
                }
            }

            routinePresetRepository.saveAll(presets);
            log.info("✅ RoutinePreset {}건 초기화 완료", presets.size());
        }
    }
}
