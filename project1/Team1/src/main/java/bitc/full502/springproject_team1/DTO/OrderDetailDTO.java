package bitc.full502.springproject_team1.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

//@Getter
//@Setter
//@Data
//public class OrderDetailDTO {
//
//    private Integer orderDetailIdx;
//    private Integer orderIdx;
//    private Integer oroductId;
//    private Integer orderDetailProductCount;
//    private Integer orderDetailPrice;
//    private String orderDetailColor;
//    private String orderDetailSize;
//}

@Getter
@Setter
@Data
public class OrderDetailDTO {

    private Integer orderDetailIdx;
    private Integer orderIdx;
    private Integer productId;
    private Integer orderDetailProductCount;
    private Integer orderDetailPrice;
    private String orderDetailColor;
    private String orderDetailSize;
}

