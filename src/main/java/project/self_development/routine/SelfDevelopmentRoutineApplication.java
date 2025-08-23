package project.self_development.routine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class SelfDevelopmentRoutineApplication {

	// ✅ 애플리케이션 전역 기본 타임존을 KST로 고정
	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	public static void main(String[] args) {
		SpringApplication.run(SelfDevelopmentRoutineApplication.class, args);
	}
}
