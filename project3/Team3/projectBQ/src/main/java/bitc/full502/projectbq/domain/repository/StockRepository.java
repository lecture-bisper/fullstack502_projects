package bitc.full502.projectbq.domain.repository;

import bitc.full502.projectbq.domain.entity.item.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<StockEntity, Long> {

    List<StockEntity> findAllByItemId(long itemId);

    @Query("SELECT s FROM StockEntity s " +
            "JOIN s.item i " +
            "JOIN s.warehouse w " +
            "JOIN i.category c " +
            "WHERE (:name IS NULL OR i.name LIKE %:name%) " +
            "AND (:manufacturer IS NULL OR i.manufacturer LIKE %:manufacturer%) " +
            "AND (:category IS NULL OR c.name LIKE %:category%) " +
            "AND (:warehouseId IS NULL OR w.id = :warehouseId)")
    List<StockEntity> searchStocks(
            @Param("name") String name,
            @Param("manufacturer") String manufacturer,
            @Param("category") String category,
            @Param("warehouseId") Long warehouseId
    );

    //    비품별 재고 조회 "20250922 완료"
    @Query("SELECT s FROM StockEntity s " +
            "JOIN s.item i " +
            "WHERE i.code = :code")
    List<StockEntity> findByCode(@Param("code") String Code);

    //    입고 등록 "20250922 완료"
    @Query("SELECT s FROM StockEntity s " +
            "JOIN s.item i " +
            "JOIN s.warehouse w " +
            "WHERE i.code = :code AND w.id = :warehouseId")
    Optional<StockEntity> findByCodeAndWarehouseId(
            @Param("code") String code,
            @Param("warehouseId") Long warehouseId
    );
}
