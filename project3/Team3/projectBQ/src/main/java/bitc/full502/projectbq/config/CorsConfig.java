package bitc.full502.projectbq.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /// *리액트 통신 CORS 허용 설정 파일*///
    /// *나중에 마지막 빌드 시 무조건 삭제 해야 됨*///

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")       // 모든 요청 경로 허용
                .allowedOrigins("http://localhost:5173")     // 모든 도메인 허용
                .allowedMethods("*")     // 모든 HTTP 메서드 허용 (GET, POST, PUT, DELETE 등)
                .allowedHeaders("*")     // 모든 헤더 허용
                .allowCredentials(true) // 인증 정보 포함 여부(false 권장)
                .maxAge(3600);
    }
}
