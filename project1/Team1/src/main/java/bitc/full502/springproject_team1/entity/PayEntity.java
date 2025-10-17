package bitc.full502.springproject_team1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="pay")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class PayEntity {

    @Id
    @Column(name = "pay_idx", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer payIdx;

    @Column(name = "order_idx_pay", nullable = false)
    private Integer orderIdx;

    @Column(name = "c_id_pay", nullable = false)
    private Integer customerId;

    @Column(name="coupon_use_yn", nullable = false, length = 45)
    private String payCouponUseyn;

    @Column(name = "point_use")
    private Integer payPointUsed;
}
