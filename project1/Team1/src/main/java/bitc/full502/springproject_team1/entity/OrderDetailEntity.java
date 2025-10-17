package bitc.full502.springproject_team1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="order_detail")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class OrderDetailEntity {

    @Id
    @Column(name = "order_detail_idx", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderDetailIdx;

    @Column(name = "p_count", nullable = false)
    private Integer orderDetailProductCount;

    @Column(name = "detail_price", nullable = false)
    private Integer orderDetailPrice;

    @Column(name = "order_color", length = 500)
    private String orderDetailColor;

    @Column(name = "order_size", length = 500)
    private String orderDetailSize;


    // (N) OrderDetail - (1) Order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_idx_detail")
    private OrderEntity order;


    // OrderDetailEntity.java
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "p_id_detail") // 외래키 이름
    private ProductEntity product;

}