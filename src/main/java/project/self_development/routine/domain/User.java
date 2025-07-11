package project.self_development.routine.domain;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    @Builder.Default
    private int firstLogin = 0;

    public  boolean checkPassword(String rawPassword, PasswordEncoder encoder){
        return encoder.matches(rawPassword, this.password);
    }
}
