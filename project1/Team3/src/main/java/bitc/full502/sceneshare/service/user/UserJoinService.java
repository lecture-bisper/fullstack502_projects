package bitc.full502.sceneshare.service.user;

import bitc.full502.sceneshare.domain.entity.user.UserEntity;

public interface UserJoinService {

  void newUser(UserEntity userIdx) throws Exception;
}
