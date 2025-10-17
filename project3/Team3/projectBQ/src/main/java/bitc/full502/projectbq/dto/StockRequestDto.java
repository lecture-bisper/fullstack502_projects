package bitc.full502.projectbq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockRequestDto {

    private String code;
    private String empCode;
    private String remark;
    private Long warehouseId;
    private Long quantity;
}
