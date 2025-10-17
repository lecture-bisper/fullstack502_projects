package bitc.full502.springproject_team1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="cart")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class CartEntity {

    @Id
    @Column(name = "cart_idx", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cartIdx;

    @Column(name = "c_id_cart", nullable = false)
    private Integer customerId;

    @Column(name = "cart_color", length = 500)
    private String cartColor;

    @Column(name="cart_size", length = 500)
    private String cartSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "p_id_cart", referencedColumnName = "p_id")
    private ProductEntity product;
}