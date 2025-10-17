package bitc.full502.springproject_team1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="wish")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class WishEntity {

    @Id
    @Column(name = "wish_idx", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer wishIdx;

    @Column(name = "c_id_wish", nullable = false)
    private Integer customerId;

    // 상품 연관관계 설정 (ProductEntity와 연결)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "p_id_wish", referencedColumnName = "p_id")
    private ProductEntity product;
    //원래 productId

    @Column(name="wish_check", nullable = false)
    private Integer wishCheck;

    @Column(name = "wish_color", length = 500)
    private String wishColor;

    @Column(name = "wish_size", length = 500)
    private String wishSize;
}
