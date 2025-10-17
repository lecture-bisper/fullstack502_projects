package bitc.full502.lostandfound.util;

import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Base64;

public class AuthUtil {

    // 비밀번호 해시 생성
    public static String convertPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    // 비밀번호 비교 (입력 평문 vs 해시값)
    public static boolean checkPassword(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }

    // 랜덤 토큰 생성
    public static String generateToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32]; // 256bit
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // 토큰 유효성 검사
    public static boolean isValidToken(LocalDate createDate) {
        return !createDate.plusDays(30).isBefore(LocalDate.now());
    }
}
