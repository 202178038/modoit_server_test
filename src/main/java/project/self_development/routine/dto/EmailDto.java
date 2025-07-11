package project.self_development.routine.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class EmailDto {
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식이 아닙니다.")
    private String email;
    @NotNull(message = "인증번호를 입력해주세요.")
    private Integer authNum;
}
