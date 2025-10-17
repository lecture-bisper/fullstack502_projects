package bitc.full502.sceneshare.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "omdb")
public record OmdbProperties(String baseUrl, String apiKey) {
}
