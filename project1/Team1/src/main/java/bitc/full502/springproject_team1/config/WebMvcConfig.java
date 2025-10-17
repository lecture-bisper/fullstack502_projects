package bitc.full502.springproject_team1.config;

import bitc.full502.springproject_team1.interceptor.LoginCheck;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration : 해당 클래스가 스프링 프로젝트의 설정 파일임을 스프링 프레임워크에 알려주는 어노테이션

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheck())
                // board, auth 밑에 있는 파일에 인터셉터 걸어주겠다.
                .addPathPatterns("/board/*")
                .addPathPatterns("/auth/*")

                // 인터셉터 동작에서 제외할 URL
                .excludePathPatterns("/board/boardList.do")
                .excludePathPatterns("/auth/join.do")
                .excludePathPatterns("/auth/joinProcess.do")
                .excludePathPatterns("/auth/login.do")
                .excludePathPatterns("/auth/loginProcess.do")
                .excludePathPatterns("/auth/logout.do")
                .excludePathPatterns("/auth/loginFail.do");
    }

    // 01)      addInterceptors : 사용자가 생성한 인터셉터를 추가
    // 02)       addInterceptor : 사용자가 생성한 인터셉터 클래스 파일 추가
    // 03)      addPathPatterns : 인터셉터를 적용할 컨트롤러의 URL 설정
    // 04)  excludePathPatterns : 인터셉터 동작에서 제외할 URL 설정
}
