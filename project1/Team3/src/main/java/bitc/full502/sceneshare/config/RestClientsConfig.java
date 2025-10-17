package bitc.full502.sceneshare.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(OmdbProperties.class)
public class RestClientsConfig {

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder
        .setConnectTimeout(Duration.ofSeconds(3))
        .setReadTimeout(Duration.ofSeconds(6))
        .build();
  }
}
