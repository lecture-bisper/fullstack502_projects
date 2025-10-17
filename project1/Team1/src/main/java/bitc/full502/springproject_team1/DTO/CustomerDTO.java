package bitc.full502.springproject_team1.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class CustomerDTO {

    private int customerIdx;
    private String customerId;
    private String customerPass;
    private String customerName;
    private String customerAddr;
    private String customerEmail;
    private String customerPhone;
    private int customerPoint;
    private String customerCoupon_yn;
}
