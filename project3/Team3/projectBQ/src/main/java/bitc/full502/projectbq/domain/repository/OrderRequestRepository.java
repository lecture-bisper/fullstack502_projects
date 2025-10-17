package bitc.full502.projectbq.domain.repository;

import bitc.full502.projectbq.domain.entity.item.OrderRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRequestRepository extends JpaRepository<OrderRequestEntity, Long> {

    @Query("SELECT o FROM OrderRequestEntity o " +
            "WHERE (:categoryId IS NULL OR o.item.category.id = :categoryId) " +
            "AND (:status IS NULL OR o.status = :status) " +
            "AND (:keyword IS NULL OR o.item.name LIKE CONCAT('%', :keyword, '%'))" +
            "AND (:manufacturer IS NULL OR o.item.manufacturer = :manufacturer) " +
            "AND (:startDate IS NULL OR o.requestDate >= :startDate) " +
            "AND (:endDate IS NULL OR o.requestDate <= :endDate) " +
            "ORDER BY o.requestDate DESC")
    List<OrderRequestEntity> findByFilters(@Param("categoryId") Long categoryId,
                                           @Param("status") String status,
                                           @Param("keyword") String keyword,
                                           @Param("manufacturer") String manufacturer,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
}

