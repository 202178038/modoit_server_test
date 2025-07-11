package project.self_development.routine.dto;

import lombok.Data;
import lombok.Setter;
import project.self_development.routine.domain.User;
@Data
@Setter
public class UserLoginResponseDto {

    private int id;
    private String name;
    private String email;
    private int firstLogin;

    public UserLoginResponseDto(User user, int firstLogin) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.firstLogin = firstLogin;
    }

}
