package project.self_development.routine.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import project.self_development.routine.dto.UserLoginRequestDto;
import project.self_development.routine.dto.UserLoginResponseDto;
import project.self_development.routine.dto.UserResponseDto;
import project.self_development.routine.dto.UserSignupRequestDto;
import project.self_development.routine.service.UserService;

@Controller
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<?> signup(@Valid @RequestBody UserSignupRequestDto dto) {
        UserResponseDto userResponseDto = userService.signup(dto);
        if(userResponseDto == null){
            return ResponseEntity.status(400).body("회원가입에 실패했습니다.");
        }
        return ResponseEntity.ok("회원가입에 성공했습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequestDto dto, HttpSession session) {
        UserLoginResponseDto userLoginResponseDto = userService.login(dto, session);
        if (userLoginResponseDto == null) {
            return ResponseEntity.status(400).body("로그인에 실패했습니다.");
        }
        return ResponseEntity.ok(userLoginResponseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("로그아웃 했습니다.");
    }
    @PostMapping("/password-reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody UserLoginRequestDto dto){
        UserResponseDto userResponseDto = userService.ResetPassword(dto);
        if(userResponseDto == null){
            return ResponseEntity.status(400).body("비밀번호 재설정 과정에서 문제가 발생하였습니다.");
        }
        return ResponseEntity.ok("비밀번호 재설정에 성공하였니다.");
    }
}

