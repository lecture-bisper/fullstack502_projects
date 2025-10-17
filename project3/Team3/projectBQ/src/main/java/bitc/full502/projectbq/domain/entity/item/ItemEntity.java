package bitc.full502.projectbq.domain.entity.item;

import bitc.full502.projectbq.common.Constants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @Column
    @Builder.Default
    private String code = "";

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String manufacturer;

    @Column(nullable = false)
    private long price;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime addDate;

    @Column(nullable = false)
    private String addUser;

    @Column(nullable = false)
    @Builder.Default
    private String approveUser = "";

    @Column(nullable = false)
    @Builder.Default
    private String status = Constants.ITEM_STATUS_PENDING;

//    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
//    private MinStockEntity minStock;

//    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
//    private StockEntity stock;
}
