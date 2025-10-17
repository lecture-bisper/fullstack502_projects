package bitc.full502.springproject_team1.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // JPA Audit 기능 활성화
public class JpaAuditingConfig {
}