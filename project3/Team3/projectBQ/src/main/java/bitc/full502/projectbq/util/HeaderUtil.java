package bitc.full502.projectbq.util;


public class HeaderUtil {

    public static String getTokenByHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header missing or invalid");
        }
        return authorizationHeader.substring(7);
    }

    public static void checkMissingToken(String webToken, String authorizationHeader) {
        if (webToken == null && authorizationHeader == null)
            throw new IllegalArgumentException("Authorization header missing or invalid");
    }
}
