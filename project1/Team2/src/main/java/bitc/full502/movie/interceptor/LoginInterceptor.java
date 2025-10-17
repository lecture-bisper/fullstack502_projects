package bitc.full502.movie.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest req , HttpServletResponse resp, Object handler) throws Exception {

        HttpSession session1 = req.getSession();

        HttpSession session = req.getSession(false);
        if(session==null || session.getAttribute("loginUser")==null){
            resp.sendRedirect("/auth/login");
            return false;

        }
        return true;
    }
}
