package bitc.full502.springproject_team1.config;

import bitc.full502.springproject_team1.interceptor.LoginCheck;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 업로드된 이미지 경로 매핑
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///C:/springboot/uploads/");
    }

    // 로그인 체크 인터셉터
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheck())
                .addPathPatterns(
                        "/board/**", // board 이하 전체 로그인 필요
                        "/boardWrite"
                )
                .excludePathPatterns(
                        "/boardList",         // ✅ 비로그인 허용 목록
                        "/login", "/logout",
                        "/css/**", "/js/**", "/img/**", "/uploads/**"
                );
    }

}


