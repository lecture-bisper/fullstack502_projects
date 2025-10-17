package bitc.full502.springproject_team1.config;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.annotation.MultipartConfig;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
public class FileUploadConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();

        // 파일 당 최대 크기
        factory.setMaxFileSize(DataSize.ofMegabytes(20)); // 20MB

        // 전체 요청당 최대 크기
        factory.setMaxRequestSize(DataSize.ofMegabytes(100)); // 100MB

        // Tomcat의 기본 파일 수 제한은 1000개라 넘지 않으면 됨
        // 파일 개수 제한은 컨트롤러 또는 JS에서 걸어야 함

        return factory.createMultipartConfig();
    }
}
