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
public class RoutinePreset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private int emotion;
    @Column(nullable = false)
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime time_slot;
    @Column(nullable = false)
    private int difficulty;
    @Column(nullable = false)
    private int category;
    @Column(nullable = false)
    private String content;

}