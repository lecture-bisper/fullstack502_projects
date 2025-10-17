package bitc.full502.springproject_team1.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

//@Getter
//@Setter
//@Data
//public class OrderDTO {
//
//    private Integer orderIdx;
//    private Integer orderProductCount;
//    private LocalDateTime orderDate;
//    private Integer productId;
//    private Integer customerId;
//    private Integer orderTotalPrice;
//    private Integer orderResPrice;
//    private Integer remainingPoint;
//    private String UsedCoupon;
//
//}

@Getter
@Setter
@Data
public class OrderDTO {

    private Integer orderIdx;
    //    private Integer orderProductCount;
    private LocalDateTime orderDate;
    private Integer productId;
    private Integer customerId;
    private Integer orderTotalPrice;
    private Integer orderResPrice;
    private Integer remainingPoint;
    private String UsedCoupon;

}
