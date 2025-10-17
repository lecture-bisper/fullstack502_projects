package bitc.full502.lostandfound.domain.repository;

import bitc.full502.lostandfound.domain.entity.FcmTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmTokenEntity, Long> {

    List<FcmTokenEntity> findAllByUserId(String userId);

    FcmTokenEntity findByUserIdAndDeviceId(String userId, String deviceId);
}
