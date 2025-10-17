package bitc.full502.springproject_team1.repository;

import bitc.full502.springproject_team1.entity.PayEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayRepository extends JpaRepository<PayEntity, Integer> {
}
