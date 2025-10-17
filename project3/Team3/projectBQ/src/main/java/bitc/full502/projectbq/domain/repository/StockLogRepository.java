package bitc.full502.projectbq.domain.repository;

import bitc.full502.projectbq.domain.entity.item.StockLogEntity;
import bitc.full502.projectbq.dto.ItemDto;
import bitc.full502.projectbq.dto.LogSearchDto;
import bitc.full502.projectbq.dto.StatisticDto;
import bitc.full502.projectbq.dto.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockLogRepository extends JpaRepository<StockLogEntity, Long> {

    @Query("""
            SELECT l
            FROM StockLogEntity l
            JOIN ItemEntity i ON l.itemId = i.id
            JOIN EmpEntity e ON l.empCode = e.code
            WHERE (:#{#dto.nameOrCode} IS NULL OR :#{#dto.nameOrCode} = ''
                   OR i.name LIKE %:#{#dto.nameOrCode}%
                   OR i.code LIKE %:#{#dto.nameOrCode}%)
              AND (:#{#dto.empCodeOrEmpName} IS NULL OR :#{#dto.empCodeOrEmpName} = ''
                   OR e.code LIKE %:#{#dto.empCodeOrEmpName}%
                   OR e.name LIKE %:#{#dto.empCodeOrEmpName}%)
              AND (:#{#dto.type} IS NULL OR :#{#dto.type} = '' OR l.type = :#{#dto.type})
              AND (:#{#dto.warehouseId} = 0 OR l.warehouse.id = :#{#dto.warehouseId})
              AND (:#{#dto.categoryId} = 0 OR i.category.id = :#{#dto.categoryId})
              AND (:#{#dto.manufacturer} IS NULL OR :#{#dto.manufacturer} = ''
                   OR i.manufacturer LIKE %:#{#dto.manufacturer}%)
              AND (:start IS NULL OR l.logDate >= :start)
              AND (:end IS NULL OR l.logDate <= :end)
            ORDER BY l.logDate DESC
            """)
    List<StockLogEntity> findAllByFilter(
            @Param("dto") LogSearchDto dto,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
            SELECT l
            FROM StockLogEntity l
            JOIN ItemEntity i ON l.itemId = i.id
            JOIN WarehouseEntity w ON l.warehouse.id = w.id
            LEFT JOIN CategoryEntity c ON i.category.id = c.id
            WHERE l.empCode = :empCode
              AND l.type = :type
              AND (:keyword IS NULL OR :keyword = ''
                   OR w.krName LIKE CONCAT('%', :keyword, '%')
                   OR i.name LIKE CONCAT('%', :keyword, '%')
                   OR i.code LIKE CONCAT('%', :keyword, '%')
                   OR i.manufacturer LIKE CONCAT('%', :keyword, '%')
                   OR c.krName LIKE CONCAT('%', :keyword, '%'))
              AND (:start IS NULL OR l.logDate >= :start)
              AND (:end IS NULL OR l.logDate <= :end)
            ORDER BY l.logDate DESC
            """)
    List<StockLogEntity> findAllByKeyword(
            @Param("empCode") String empCode,
            @Param("type") String type,
            @Param("keyword") String keyword,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
                SELECT new bitc.full502.projectbq.dto.StatisticDto(
                    null,
                    CAST(l.itemId AS string),
                    l.itemId,
                    SUM(l.quantity),
                    0,
                    MAX(l.logDate)
                )
                FROM StockLogEntity l
                WHERE l.type = 'OUT'
                  AND (:startDate IS NULL OR l.logDate >= :startDate)
                  AND (:endDate IS NULL OR l.logDate <= :endDate)
                GROUP BY l.itemId
                ORDER BY MAX(l.logDate) DESC
            """)
    List<StatisticDto<ItemDto>> findDistinctByItemId(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
                SELECT new bitc.full502.projectbq.dto.StatisticDto(
                    null,
                    l.empCode,
                    l.itemId,
                    SUM(l.quantity),
                    0,
                    MAX(l.logDate)
                )
                FROM StockLogEntity l
                WHERE l.type = 'OUT'
                  AND (:startDate IS NULL OR l.logDate >= :startDate)
                  AND (:endDate IS NULL OR l.logDate <= :endDate)
                GROUP BY l.empCode, l.itemId
                ORDER BY MAX(l.logDate) DESC
            """)
    List<StatisticDto<UserDto>> findDistinctByEmpCode(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
