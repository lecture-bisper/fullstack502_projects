package bitc.full502.sceneshare.service.user;

import bitc.full502.sceneshare.domain.entity.user.UserEntity;
import bitc.full502.sceneshare.domain.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserJoinServiceImpl implements UserJoinService {

  private final UserRepository userRepository;
  @Override
  public void newUser(UserEntity userIdx) throws Exception {
    userRepository.save(userIdx);
  }
}
