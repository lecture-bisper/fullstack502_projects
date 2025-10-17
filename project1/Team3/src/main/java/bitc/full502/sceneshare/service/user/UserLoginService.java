package bitc.full502.sceneshare.service.user;

import bitc.full502.sceneshare.domain.entity.user.UserEntity;

public interface UserLoginService {

  int isUserInfo(String userId, String userPw) throws Exception;

  UserEntity selectUserInfo(String userId) throws Exception;

  int isUserId(String userId) throws Exception;
}
