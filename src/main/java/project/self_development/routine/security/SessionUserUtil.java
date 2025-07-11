package project.self_development.routine.security;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import project.self_development.routine.domain.User;
import project.self_development.routine.security.CustomUserDetails;

public class SessionUserUtil {

    public static User getSessionUser(HttpSession session) {
        Object context = session.getAttribute("SPRING_SECURITY_CONTEXT");
        if (context instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getUser();
        }

        return ((CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()).getUser();
    }
}
