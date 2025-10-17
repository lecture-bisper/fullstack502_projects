package bitc.full502.lostandfound.service;

import bitc.full502.lostandfound.domain.entity.TokenEntity;
import bitc.full502.lostandfound.domain.entity.UserEntity;
import bitc.full502.lostandfound.domain.repository.TokenRepository;
import bitc.full502.lostandfound.domain.repository.UserRepository;
import bitc.full502.lostandfound.dto.UserDTO;
import bitc.full502.lostandfound.util.AuthUtil;
import bitc.full502.lostandfound.util.Constants;
import bitc.full502.lostandfound.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;


@Service
@RequiredArgsConstructor
public class JpaServiceImpl implements JpaService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Override
    public String isDuplicateUserData(String checkType, String userData) throws Exception {

        return switch (checkType) {
            case Constants.CHECK_DUPLICATE_ID ->
                    userRepository.existsById(userData) ? Constants.EXIST : Constants.NOT_EXIST;
            case Constants.CHECK_DUPLICATE_NAME ->
                    userRepository.existsByUserName(userData) ? Constants.EXIST : Constants.NOT_EXIST;
            case Constants.CHECK_DUPLICATE_EMAIL ->
                    userRepository.existsByEmail(userData) ? Constants.EXIST : Constants.NOT_EXIST;
            default -> Constants.NOT_EXIST;
        };
    }

    @Override
    public void createUser(UserEntity user) throws Exception {
        user.setPassword(AuthUtil.convertPassword(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public String loginUser(String userId, String password, String role, boolean isAutoLogin) throws Exception {
        if (userId.equals("admin")) role = Constants.ROLE_ADMIN;
        UserEntity user = userRepository.findByUserIdAndRole(userId, role);

        if (user != null) {
            user.setAutoLogin(isAutoLogin);
            userRepository.save(user);

            if (AuthUtil.checkPassword(password, user.getPassword())) {
                if (tokenRepository.findByUser_UserId(userId) == null) {
                    TokenEntity token = new TokenEntity();
                    token.setUser(user);
                    token.setToken(AuthUtil.generateToken());
                    tokenRepository.save(token);

                    return token.getToken();
                } else {
                    return tokenRepository.findByUser_UserId(userId).getToken();
                }
            }
        }

        return Constants.FAILURE;
    }

    @Override
    @Transactional
    public String validateToken(String token) throws Exception {
        TokenEntity findToken = tokenRepository.findByToken(token);
        if (findToken == null) return Constants.FAILURE;

        UserEntity user = userRepository.findById(findToken.getUser().getUserId()).orElse(null);

        // Null 체크
        if (user != null) {
            // 자동 로그인 체크
            if (user.isAutoLogin()) {
                // 날짜 유효성 검사
                if (AuthUtil.isValidToken(findToken.getCreateDate())) {
                    return Constants.SUCCESS;
                }
            } else {
                tokenRepository.deleteByToken(token);
                return Constants.AUTO_LOGIN_DISABLED;
            }
        }
        tokenRepository.deleteByToken(token);
        return Constants.FAILURE;
    }

    @Override
    @Transactional
    public String logoutUser(String token) throws Exception {
        tokenRepository.deleteByToken(token);
        return Constants.SUCCESS;
    }

    @Override
    public String getUserIdByToken(String token) throws Exception {
        return tokenRepository.findByToken(token).getUser().getUserId();
    }

    @Override
    public UserDTO getUserInfo(String userId) throws Exception {
        UserEntity user = userRepository.findById(userId).orElse(null);
        return UserUtil.convertToUserDTO(Objects.requireNonNull(user));
    }

    @Override
    public boolean changePassword(String token, String oldPassword, String newPassword, String newPasswordConfirm) throws Exception {
        String userId = tokenRepository.findByToken(token).getUser().getUserId();
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;

        // 로그인 된 계정 비밀번호와 입력받은 현재 비밀번호 비교
        if (AuthUtil.checkPassword(oldPassword, user.getPassword())) {
            // 새 비밀번호와 새 비밀번호 확인 비교
            if (newPassword.equals(newPasswordConfirm)) {
                // 비밀번호 해쉬값으로 변환 후 저장
                user.setPassword(AuthUtil.convertPassword(newPassword));
                userRepository.save(user);
                return true;
            }
        }

        return false;
    }

    @Override
    @Transactional
    public String deleteUser(String token) throws Exception {
        String userId = tokenRepository.findByToken(token).getUser().getUserId();
        userRepository.deleteById(userId);
        return Constants.SUCCESS;
    }
}
