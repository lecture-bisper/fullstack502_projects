package bitc.full502.springproject_team1.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="customer")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class CustomerEntity {


    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int customerIdx;
    @Column(name = "user_id")
    private String customerId;
    @Column(name = "pass", nullable = false, length = 500)
    private String customerPass;
    @Column(name = "c_name", nullable = false , length = 500)
    private String customerName;
    @Column(name = "addr", nullable = false , length = 500)
    private String customerAddr;
    @Column(name = "email" , length = 500)
    private String customerEmail;
    @Column(name = "phone" , length = 500)
    private String customerPhone;
    @Column(name = "point")
    private int customerPoint;
    @Column(name = "coupon_yn", nullable = false, length = 50)
    private String customerCoupon_yn;



    // Customer 1 ─── (N) Order
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<OrderEntity> orderList = new ArrayList<>();
}
