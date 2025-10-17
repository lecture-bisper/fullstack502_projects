package bitc.full502.projectbq.domain.entity.item;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class StockLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // 다대다 관계 시 중간 엔티티 추가 생성해야돼서 FK 연결 안하고 그냥 적는걸로 했습니다ㅎ..
    @Column(nullable = false)
    private long itemId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private WarehouseEntity warehouse;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String empCode;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime logDate;

    @Column(nullable = false)
    private long quantity;

    @Column
    @Builder.Default
    private String memo = "";
}
