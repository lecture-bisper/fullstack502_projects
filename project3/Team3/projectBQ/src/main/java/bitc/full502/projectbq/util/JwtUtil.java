package bitc.full502.projectbq.util;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JwtUtil {

    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            "fullstack502team3finalproject123456".getBytes()
    );

    // expireMillis 는 밀리초
    public static String generateToken(String empCode, long expireMillis) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(empCode)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expireMillis))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static Claims validateToken(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static String getEmpCode(String token) {
        printExpiration(token);
        return validateToken(token).getSubject();
    }

    // 디버깅용: 토큰 만료시간 콘솔 출력
    public static void printExpiration(String token) {
        try {
            Claims claims = validateToken(token);
            System.out.println("사용자 코드: " + claims.getSubject() + " / 토큰 만료 시간: "
                    + new SimpleDateFormat("yyyy년 MM월 dd일 E요일 HH시 mm분 ss초", Locale.KOREAN).format(claims.getExpiration()));
        } catch (ExpiredJwtException e) {
            System.out.println("토큰이 이미 만료됨. 만료 시간: "
                    + new SimpleDateFormat("yyyy년 MM월 dd일 E요일 HH시 mm분 ss초", Locale.KOREAN).format(e.getClaims().getExpiration()));
        } catch (JwtException e) {
            System.out.println("유효하지 않은 토큰");
        }
    }
}
