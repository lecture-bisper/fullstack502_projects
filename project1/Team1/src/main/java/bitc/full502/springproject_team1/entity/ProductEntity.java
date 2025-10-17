package bitc.full502.springproject_team1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class ProductEntity {

//    @Id
//    @Column(name = "p_id", nullable = false)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    // 데이터베이스에서 자동 증가는 아니지만, 자동으로 들어가줘야하기 때문에 위 어노테이션 넣었습니다!
//    private int productId;
//
//    @Column(name = "p_price", nullable = false)
//    private int productPrice;
//
//    @Column(name = "p_name", nullable = false)
//    private String productName;
//
//    @Column(name = "p_code", nullable = false)
//    private String productCode;
//
//    @Column(name = "p_brand", nullable = false)
//    private String productBrand;
//
//    @Column(name = "p_thumbnail", length = 255)
//    private String productThumnail;
//
//    @Column(name = "p_color", length = 255)
//    private String productColor;
//
//    @Column(name = "p_size", length = 255)
//    private String productSize;
//
//    @Column(name="p_image_1", length = 255)
//    private String productImage1;
//
//    @Column(name="p_image_2", length = 255)
//    private String productImage2;
//
//    @Column(name="p_image_3", length = 255)
//    private String productImage3;
//
//    @Column(name="p_image_4", length = 255)
//    private String productImage4;
//
//    @Column(name="p_image_5", length = 255)
//    private String productImage5;
//
//
//    // (1) Product - (N) OrderDetail
//    @OneToMany(mappedBy = "product")
//    private List<OrderDetailEntity> orderDetailList = new ArrayList<>();

    @Id
    @Column(name = "p_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 데이터베이스에서 자동 증가는 아니지만, 자동으로 들어가줘야하기 때문에 위 어노테이션 넣었습니다!
    private Integer productId;

    @Column(name = "p_price", nullable = false)
    private int productPrice;

    @Column(name = "p_name", nullable = false)
    private String productName;

    @Column(name = "p_code", nullable = false)
    private String productCode;

    @Column(name = "p_brand", nullable = false)
    private String productBrand;

    @Column(name = "p_thumbnail", length = 255)
    private String productThumnail;

    @Column(name = "p_color", length = 255)
    private String productColor;

    @Column(name = "p_size", length = 255)
    private String productSize;

    @Column(name="p_image_1", length = 255)
    private String productImage1;

    @Column(name="p_image_2", length = 255)
    private String productImage2;

    @Column(name="p_image_3", length = 255)
    private String productImage3;

    @Column(name="p_image_4", length = 255)
    private String productImage4;

    @Column(name="p_image_5", length = 255)
    private String productImage5;


    // (1) Product - (N) OrderDetail
    @OneToMany(mappedBy = "product")
    private List<OrderDetailEntity> orderDetailList = new ArrayList<>();

    @Transient
    public List<String> getImageList() {
        List<String> list = new ArrayList<>();
        if (productImage1 != null && !productImage1.isEmpty()) list.add(productImage1);
        if (productImage2 != null && !productImage2.isEmpty()) list.add(productImage2);
        if (productImage3 != null && !productImage3.isEmpty()) list.add(productImage3);
        if (productImage4 != null && !productImage4.isEmpty()) list.add(productImage4);
        if (productImage5 != null && !productImage5.isEmpty()) list.add(productImage5);
        return list;
    }

    // 별칭 getter
    public Integer getPId() {
        return this.productId;
    }
}
