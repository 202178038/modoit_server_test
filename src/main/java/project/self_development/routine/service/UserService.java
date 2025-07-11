package project.self_development.routine.service;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import project.self_development.routine.domain.User;
import project.self_development.routine.dto.UserLoginRequestDto;
import project.self_development.routine.dto.UserLoginResponseDto;
import project.self_development.routine.dto.UserResponseDto;
import project.self_development.routine.dto.UserSignupRequestDto;
import project.self_development.routine.repository.UserRepository;
import project.self_development.routine.security.CustomUserDetails;

import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserResponseDto signup(UserSignupRequestDto dto){
        if(userRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new IllegalArgumentException("가입된 이메일입니다.");
        }
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
        return new UserResponseDto(user);
    }
    public UserLoginResponseDto login(UserLoginRequestDto dto, HttpSession session) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());

        // 인증 처리: 내부적으로 UserDetailsService, PasswordEncoder까지 호출됨
        Authentication auth = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(auth);

        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();
        int loginStatus = user.getFirstLogin();

        // 최초 로그인이라면 firstLogin을 1로 업데이트
        if (user.getFirstLogin() == 0) {
            user.setFirstLogin(1);
            userRepository.save(user); // firstLogin 값 업데이트 저장
        }

        return new UserLoginResponseDto(user, loginStatus);
    }

    public UserResponseDto ResetPassword(UserLoginRequestDto dto) {
        Optional<User> userByEmail = userRepository.findByEmail(dto.getEmail());
        if(userByEmail.isEmpty()){
            throw new UsernameNotFoundException("해당 이메일의 사용자를 찾을 수 없습니다: " + dto.getEmail());
        }
        User user = userByEmail.get();
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);

        return new UserResponseDto(user);
    }

}
