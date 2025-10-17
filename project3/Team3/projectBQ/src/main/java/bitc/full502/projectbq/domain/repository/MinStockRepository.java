package bitc.full502.projectbq.domain.repository;

import bitc.full502.projectbq.domain.entity.item.ItemEntity;
import bitc.full502.projectbq.domain.entity.item.MinStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MinStockRepository extends JpaRepository<MinStockEntity, Long> {
    MinStockEntity findByItem(ItemEntity item);
}
