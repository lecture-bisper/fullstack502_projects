package bitc.full502.springproject_team1.repository;

import bitc.full502.springproject_team1.entity.HistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryRepository extends JpaRepository<HistoryEntity,Integer> {

    List<HistoryEntity> findTop5ByCustomerIdOrderByHistoryDateDesc(int customerId);

    void deleteByCustomerIdAndProductIdx(int customerId, int productIdx); // 필드명에 맞게
}
