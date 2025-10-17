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
@Table(name = "order_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class OrderRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEntity item;

    @Column
    private String comment;

    @Column(nullable = false)
    private long requestQty;

    @Column(nullable = false)
    private String requestUser;

    @Column
    private String approveUser;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime requestDate;

    @Column(nullable = false)
    @Builder.Default
    private String status = Constants.REQUEST_STATUS_REQUESTED;
}
