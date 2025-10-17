package bitc.full502.springproject_team1.DTO;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class PayDTO {

    private Integer payIdx;
    private Integer orderIdx;
    private Integer customerId;
    private String payCouponUsed;
    private Integer payPointUsed;
}
