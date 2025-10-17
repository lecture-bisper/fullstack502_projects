package bitc.full502.movie.config;

import bitc.full502.movie.domain.entity.UserEntity;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Component
@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("loginUser")
    public UserEntity loginUser(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("loginUser");
        return user != null ? user : new UserEntity();  // 기본 객체 반환
    }

    @ModelAttribute("keyword")
    public String keyword(HttpSession session) {
        return (String) session.getAttribute("keyword");
    }
}
