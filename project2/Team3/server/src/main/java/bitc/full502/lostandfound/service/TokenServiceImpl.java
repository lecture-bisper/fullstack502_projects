package bitc.full502.lostandfound.service;

import bitc.full502.lostandfound.domain.entity.FcmTokenEntity;
import bitc.full502.lostandfound.domain.entity.TokenEntity;
import bitc.full502.lostandfound.domain.repository.FcmTokenRepository;
import bitc.full502.lostandfound.domain.repository.TokenRepository;
import bitc.full502.lostandfound.util.AuthUtil;
import bitc.full502.lostandfound.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;
    private final FcmTokenRepository fcmTokenRepository;

    @Override
    public boolean isValidToken(String actualToken) throws Exception {
        TokenEntity findToken = tokenRepository.findByToken(actualToken);
        if (findToken == null) return false;

        return AuthUtil.isValidToken(findToken.getCreateDate());
    }

    @Override
    public String getUserNameByToken(String token) throws Exception {
        return tokenRepository.findUserUserIdByToken(token);
    }

    @Override
    @Transactional
    public String saveFcmToken(String userToken, String fcmToken, String deviceId) throws Exception {
        String userId = tokenRepository.findUserUserIdByToken(userToken);
        FcmTokenEntity fcmTokenEntity = fcmTokenRepository.findByUserIdAndDeviceId(userId, deviceId);

        if (fcmTokenEntity == null) {
            fcmTokenRepository.save(new FcmTokenEntity(userId, fcmToken, deviceId));
        } else {
            fcmTokenEntity.setToken(fcmToken);
            fcmTokenRepository.save(fcmTokenEntity);
        }

        return Constants.SUCCESS;
    }

    @Override
    public List<String> getFcmTokens(String userToken) {
        return fcmTokenRepository.findAllByUserId(tokenRepository.findUserUserIdByToken(userToken))
                .stream()
                .map(FcmTokenEntity::getToken)
                .collect(Collectors.toList());
    }
}
