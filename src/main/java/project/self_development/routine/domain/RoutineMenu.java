package project.self_development.routine.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoutineMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime timeSlot;
    @Builder.Default
    private boolean isActive = true;
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "preset_id")
    private RoutinePreset preset;
    @ManyToOne
    @JoinColumn(name = "custom_id")
    private RoutineDefinition custom;

    @PrePersist
    public void checkSingleSource() {
        if ((preset == null && custom == null) || (preset != null && custom != null)) {
            throw new IllegalStateException("RoutineMenu는 preset 또는 custom 중 하나만 지정해야 합니다.");
        }
    }
}