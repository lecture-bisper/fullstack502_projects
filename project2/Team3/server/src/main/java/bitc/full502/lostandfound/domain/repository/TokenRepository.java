package bitc.full502.lostandfound.domain.repository;

import bitc.full502.lostandfound.domain.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

    TokenEntity findByUser_UserId(String userUserId) throws Exception;

    TokenEntity findByToken(String token) throws Exception;

    @Modifying
    @Query("DELETE FROM TokenEntity t WHERE t.token = :token")
    void deleteByToken(String token) throws Exception;

    @Query("SELECT t.user.userId FROM TokenEntity t WHERE t.token = :token")
    String findUserUserIdByToken(String token);
}
