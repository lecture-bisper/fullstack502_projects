package bitc.full502.lostandfound.domain.repository;

import bitc.full502.lostandfound.domain.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

    List<BoardEntity> findAllByOrderByIdxDesc();

    @Query(
            "SELECT b FROM BoardEntity b " +
                    "LEFT JOIN b.category c " +
                    "WHERE (:keyword IS NULL OR TRIM(:keyword) = '' OR " +
                    "       LOWER(b.title)       LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "       LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "       LOWER(b.ownerName)   LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                    "  AND (:categoryId IS NULL OR c.categoryId = :categoryId) " +
                    "  AND (:type IS NULL OR b.type = :type)" +
                    "AND (:fromDate IS NULL OR b.eventDate >= :fromDate) " +
                    "AND (:toDate IS NULL OR b.eventDate <= :toDate) " +
                    "ORDER BY b.idx DESC")
    List<BoardEntity> search(
            @Param("keyword") String keyword,
            @Param("categoryId") Integer categoryId,
            @Param("type") String type,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}
