package bitc.full502.sceneshare.domain.repository.user;

import bitc.full502.sceneshare.domain.entity.dto.MovieInfoDTO;
import bitc.full502.sceneshare.domain.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

  UserEntity findByUserId(String userId) throws Exception;

  int countByUserIdAndUserPw(String userId, String userPw) throws Exception;

  int countByUserId(String userId) throws Exception;

  List<UserEntity> findByUserIdIn(Collection<String> userIds);
}
