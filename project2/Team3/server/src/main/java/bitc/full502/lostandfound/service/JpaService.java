package bitc.full502.lostandfound.service;

import bitc.full502.lostandfound.domain.entity.UserEntity;
import bitc.full502.lostandfound.dto.UserDTO;

public interface JpaService {

    String isDuplicateUserData(String checkType, String userData) throws Exception;

    void createUser(UserEntity user) throws Exception;

    String loginUser(String userId, String password, String role, boolean isAutoLogin) throws Exception;

    String validateToken(String token) throws Exception;

    String logoutUser(String token) throws Exception;

    String getUserIdByToken(String token) throws Exception;

    UserDTO getUserInfo(String userId) throws Exception;

    boolean changePassword(String token, String oldPassword, String newPassword, String newPasswordConfirm) throws Exception;

    String deleteUser(String token) throws Exception;
}
