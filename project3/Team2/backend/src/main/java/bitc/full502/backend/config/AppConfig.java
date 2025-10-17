package bitc.full502.backend.config;

import bitc.full502.backend.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class AppConfig implements WebMvcConfigurer {

    private final JwtFilter jwtFilter;
    
    @Value("${app.upload.profile.dir}")
    private String uploadDir;
    
    @Value("${app.upload.product.dir}")
    private String productUploadDir;

    public AppConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    // ===== WebMvc: 리소스 핸들링 ===== 진경 수정
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/profile/**")
                .addResourceLocations("file:" + uploadDir + "/");
        registry.addResourceHandler("/uploads/product/**")
                .addResourceLocations("file:" + productUploadDir + "/");
    }

  // ===== Security: JWT + CORS =====
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 개발용 CORS
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                    "/api/agency-items/**",
                    "/api/agencyorder/register",
                    "/api/agencyorder/android",
                    "/api/agencyorder/orders/**",
                    "/api/agencyorder/confirm",
                "/api/login",
                "/api/login/**",
                "/api/head/signup",
                "/api/head/mypage/**",
                "/api/head/checkEmail",
                "/uploads/profile/**",
                "/api/auth/findPw",
                "/api/auth/resetPw",
                "/api/users/check-id",
                "/api/users/check-email",
                "/api/users/register",
                "/api/users/list",
                "/api/users/delete",
                "/api/users/check-company",
                "/api/products",
                "/api/products/**",
                "/api/agencyproducts",
                "/api/logisticproducts",
                    "/api/logisticproduct",
                "/api/head/mypage/**",
                "/api/agency/",
                "/api/agency/mypage/**",
                "/api/agency/*/products",
                "/api/agencyorder/full",
                "/api/agencies/**",
                "/api/agencies",
                "/api/deliveries/**",
                "/api/deliveries",
                "/api/notices",
                "/api/notices/**",
                "/api/products/**",
                "/api/products",
                "/api/agency/update",
                "/api/agencyorder/**",
                "/api/deliveries",
                "/api/logistic/mypage/**",
                "/api/logistic/update",
                "/api/agency/agencyproducts",
                "/api/products",
                "/api/agency",
                "/api/agency/update",
                "/api/agency/register",
                "/api/agency/mypage/**",
                "/api/agency/*/products",
                "/api/agency/agencyproducts",
                    "/api/agencyorder/draft",
                "/api/agencyorder/full",
                "/api/agencyproducts",
                "/api/deliveries",
                "/api/logistic/update",
                "/api/logistic/register",
                "/api/logistic/mypage/**",
                "/api/logisticproducts",
                "/api/dashboard/monthly",
                "/api/notices",
                "/api/orders",
                "/api/orders/**",
                "/api/orders/items",
                "/api/orders/items/**",
                "/api/status",
                "/uploads/**",
                "/api/orders/confirm",
                "/api/agencyorder/confirm",
                "/api/status",
                "/api/login",
                                "/api/login/**",
                                "/api/head/signup",
                                "/uploads/**", // 진경 추가
                                "/uploads/profile/**",
                                "/uploads/product/**", // 진경 추가
                                "/api/head/checkEmail",
                                "/api/auth/findPw",
                                "/api/auth/resetPw",
                                "/api/users/check-id",
                                "/api/users/check-email",
                                "/api/users/register",
                                "/api/users/list",
                                "/api/users/delete",
                                "/api/products",
                                "/api/products/**", // 진경 추가
                                "/api/agencyproducts",
                                "/api/logisticproducts",
                                "/api/logisticproducts/**", // 진경 추가
                                "/api/head/mypage/**",
                                "/api/agency/mypage/**",
                                "/api/agency",
                                "/api/agency/**", // 진경 추가
                                "/api/agency/*/products", // 찬우 추가
                                "/api/agencyorder/full",  // <- 여기 추가
                                "/api/agency/update",
                                "/api/agencyorder/**",
                                "/api/deliveries",
                                "/api/logistic/mypage/**",
                                "/api/logistic/update",
                                // 정빈 추가 시작
                                "/api/agency/agencyproducts",
                                "/api/orders",
                                "/api/orders/**",
                                "/api/orders/items",
                                "/api/orders/items/**",
                                "/api/status",
                                // 정빈 추가 끝
                                // 진경 추가 시작
                                "/api/notices/**",
                                "/api/agency-items/**",
                                "/api/logistic-store/**",
                                "/api/dashboard/**",
                                // 진경 추가 끝
                "/api/agency-items/**"
            ).permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


  // ===== Security: 비밀번호 인코더 =====
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

    // ===== Security: CORS 설정 (개발용, 모든 허용) =====
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // 모든 프론트 허용
        configuration.setAllowedMethods(Arrays.asList("*"));          // 모든 HTTP 메서드 허용
        configuration.setAllowedHeaders(Arrays.asList("*"));          // 모든 헤더 허용
        configuration.setAllowCredentials(true);                      // 쿠키 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}