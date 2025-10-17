package bitc.full502.projectbq.domain.repository;

import bitc.full502.projectbq.domain.entity.item.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {

    //    비품 List (20250917 완료)
    List<ItemEntity> findAllByOrderByAddDateDesc();

    //   웹 비품 검색 필터링 Query (20250917 완료)
    @Query("SELECT i FROM ItemEntity i " +
            "WHERE (:name IS NULL OR i.name LIKE %:name%) " +
            "AND (:manufacturer IS NULL OR i.manufacturer LIKE %:manufacturer%) " +
            "AND (:code IS NULL OR i.code LIKE %:code%) " +
            "AND (:categoryId IS NULL OR i.category.id = :categoryId) " +
            "AND (:minPrice IS NULL OR i.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR i.price <= :maxPrice) " +
            "AND (:startDate IS NULL OR i.addDate >= :startDate) " +
            "AND (:endDate IS NULL OR i.addDate <= :endDate) " +
            "AND (:status IS NULL OR i.status = :status) " +  // status 조건 추가
            "ORDER BY i.addDate DESC")
    List<ItemEntity> searchItems(
            @Param("name") String name,
            @Param("manufacturer") String manufacturer,
            @Param("code") String code,
            @Param("categoryId") Long categoryId,
            @Param("minPrice") Long minPrice,
            @Param("maxPrice") Long maxPrice,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") String status
    );


    //    앱 검색 "20250918 완료"
    @Query("SELECT DISTINCT i FROM ItemEntity i " +
            "JOIN FETCH i.category c " +
            "WHERE (:keyword IS NULL OR i.name LIKE CONCAT('%', :keyword, '%') " +
            "   OR i.manufacturer LIKE CONCAT('%', :keyword, '%') " +
            "   OR i.code LIKE CONCAT('%', :keyword, '%')) " +
            "ORDER BY i.addDate DESC")
    List<ItemEntity> searchItemsAll(@Param("keyword") String keyword);


    //    비품 상세조회 (비품코드) "20250917 완료"
    ItemEntity findByCode(String code);


    //    발주 요청
    @Query("SELECT i FROM ItemEntity i " +
            "JOIN MinStockEntity m ON m.item = i " +
            "WHERE (:itemStatus IS NULL OR i.status = :itemStatus) " +
            "AND (:keyword IS NULL OR i.name LIKE %:keyword%) " +
            "AND (:manufacturer IS NULL OR i.manufacturer LIKE %:manufacturer%) " +
            "AND (:categoryId IS NULL OR i.category.id = :categoryId) " +
            "AND (:startDate IS NULL OR i.addDate >= :startDate) " +
            "AND (:endDate IS NULL OR i.addDate <= :endDate) " +
            "AND (:minStockStatus IS NULL OR m.status = :minStockStatus) " +
            "ORDER BY i.addDate DESC")
    List<ItemEntity> findByStatus(@Param("itemStatus") String itemStatus,
                                  @Param("keyword") String keyword,
                                  @Param("manufacturer") String manufacturer,
                                  @Param("categoryId") Long categoryId,
                                  @Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate,
                                  @Param("minStockStatus") String minStockStatus);
}
