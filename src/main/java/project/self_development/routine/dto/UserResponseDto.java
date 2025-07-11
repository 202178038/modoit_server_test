package project.self_development.routine.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import project.self_development.routine.domain.User;

@Data
@Setter
public class UserResponseDto {
    private int id;
    private String name;
    private String email;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
    }
}
