package bitc.full502.lostandfound.domain.repository;

import bitc.full502.lostandfound.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    boolean existsByUserName(String userName) throws Exception;

    boolean existsByEmail(String email) throws Exception;

    UserEntity findByUserIdAndRole(String userId, String role);
}
