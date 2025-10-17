package bitc.full502.springproject_team1.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class CartDTO {

    private Integer cartIdx;
    private Integer customerId;
    private Integer productId;
    private String cartColor;
    private String cartSize;
}
