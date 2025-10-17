package bitc.full502.projectbq.domain.entity.item;

import bitc.full502.projectbq.common.Constants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "min_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MinStockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEntity item;

    @Column(nullable = false)
    @Builder.Default
    private long standardQty = 0L;

    @Column(nullable = false)
    @Builder.Default
    private long safetyQty = 0L;

    @Column(nullable = false)
    @Builder.Default
    private String status = Constants.MIN_STOCK_STATUS_OK;
}
