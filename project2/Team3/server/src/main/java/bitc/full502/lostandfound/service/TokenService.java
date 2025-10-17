package bitc.full502.lostandfound.service;

import java.util.List;

public interface TokenService {

    boolean isValidToken(String actualToken) throws Exception;

    String getUserNameByToken(String token) throws Exception;

    String saveFcmToken(String userId, String fcmToken, String deviceId) throws Exception;

    List<String> getFcmTokens(String userId) throws Exception;
}
