package project.self_development.routine.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import project.self_development.routine.domain.State;
import project.self_development.routine.domain.User;
import project.self_development.routine.dto.StateRequestDto;
import project.self_development.routine.repository.StateRepository;
import project.self_development.routine.repository.UserRepository;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class StateService {
    private final StateRepository stateRepository;
    private final UserRepository userRepository;

    public void AnalyzeEmotions(int userId, StateRequestDto dto){
        LocalDate date =  LocalDate.now();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        if (stateRepository.existsByUserAndDate(user, date)) {
            throw new IllegalStateException("해당 날짜의 상태 정보가 이미 존재합니다.");
        }


        State state = new State();
        state.setEmotion(dto.getEmotion());
        state.setIntensity(dto.getIntensity());
        state.setDate(date);
        state.setUser(user);

        stateRepository.save(state); // 저장까지 수행
    }
}
