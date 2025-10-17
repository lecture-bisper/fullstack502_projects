package bitc.full502.springproject_team1.DTO;


import bitc.full502.springproject_team1.entity.ProductEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Data
public class ProductDTO {

    private int productId;
    private int productPrice;
    private String productName;
    private String productCode;
    private String productBrand;
    private String productColor;
    private String productSize;
    private String productThumnail;
    private String productImage1;
    private String productImage2;
    private String productImage3;
    private String productImage4;
    private Integer historyIdx;


    public ProductDTO() {}

    public ProductDTO(ProductEntity e, Integer historyIdx) {
        this.productId = e.getProductId();
        this.productName = e.getProductName();
        this.productPrice = e.getProductPrice();
        this.productThumnail = e.getProductThumnail();
        this.historyIdx = historyIdx; // 추가 삭제버튼 때문에
    }

}
