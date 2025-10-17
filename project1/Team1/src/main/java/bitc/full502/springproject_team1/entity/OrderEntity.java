package bitc.full502.springproject_team1.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"customer", "orderDetailList"})

public class OrderEntity {
    @Id
    @Column(name = "order_idx", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderIdx;

    @Column(name = "order_date")
    private LocalDateTime orderDate = LocalDateTime.now();

    @Column(name = "total_price", nullable = false)
    private Integer orderTotalPrice;

    @Column(name = "res_price", nullable = false)
    private Integer orderResPrice;

    @Column(name = "remaining_point")
    private Integer remainingPoint;

    @Column(name = "used_coupon", nullable = false)
    private String usedCoupon = "n";

    @Column(name = "p_count")
    private Integer productCount;


    // 고객 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "c_id_order", referencedColumnName = "id") // FK 연결 정확히
    private CustomerEntity customer;

    // 주문 상세
    @JsonManagedReference
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetailEntity> orderDetailList = new ArrayList<>();

}
