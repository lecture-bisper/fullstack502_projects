package bitc.full502.springproject_team1.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

// Interceptor 를 사용하기 위해 HandlerInterceptor 인터페이스를 상속 받아야 함

// HandlerInterceptor
//  - 01) preHandle() : 지정한 컨트롤러가 동작되기 이전에 먼저 수행
//  - 02) postHandle() : 지정한 컨트롤러가 동작 후, View 가 동작되기 이전에 수행
//  - 03) afterCompletion() : 모든 동작이 완료된 후 수행

public class LoginCheck implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception{

        // HttpServletRequest 에서 세션 정보를 가져옴
        HttpSession session = req.getSession();

        System.out.println("============= Interceptor 동작 =============");

        // 세션에 저장되어 있는 정보 가져오기
        String userId = (String)session.getAttribute("customerId");

        System.out.println("사용자 ID : "  + userId);
        System.out.println("\n********* Interceptor *********");

        // 가져온 세션 정보를 통해서 로그인 상태 확인
        if (userId == null || userId.isEmpty()) {
            System.out.println("비로그인 상태");
            System.out.println((String) session.getAttribute("customerId"));
            // 요청 URL 저장 (ex. /boardList, /board/detail/5 등)
            String requestURI = req.getRequestURI();
            String queryString = req.getQueryString();
            if (queryString != null) {
                requestURI += "?" + queryString;
            }

            session.setAttribute("redirectAfterLogin", requestURI);
            System.out.println("리다이렉트 대상 저장: " + requestURI);

            // 비로그인 상태이므로 로그인이 가능한 페이지로 이동 시킴
            resp.sendRedirect("/login");

            // 인터셉터를 통과 X, false 를 반환 -> false 일 경우 원하는 컨트롤러에 접근 불가능
            return false;
        }

        else {
            System.out.println("로그인 상태");

            // 로그인 상태이므로 세션 유지 시간을 다시 설정
            session.setMaxInactiveInterval(60 * 10);

            // 인터셉터를 통과 O, true 를 반환 -> 원하는 컨트롤러에 접근 가능
            return true;
        }
    }
}
