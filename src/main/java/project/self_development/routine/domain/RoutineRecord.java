package project.self_development.routine.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoutineRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "routine_menu_id", nullable = false)
    private RoutineMenu routineMenu;


    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Builder.Default
    private boolean isCompleted = false;

}
