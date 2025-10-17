package bitc.full502.springproject_team1.DTO;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class WishDTO {

    private Integer wishIdx;
    private Integer customerId;
    private Integer productId;
    private Integer wishCheck;
    private String wishColor;
    private String wishSize;
}
