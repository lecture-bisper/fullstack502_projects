package bitc.full502.movie.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// JPA Audit 기능을 사용하기 위한 설정 파일

@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {
}
