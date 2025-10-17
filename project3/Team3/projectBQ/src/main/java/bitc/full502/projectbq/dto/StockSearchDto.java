package bitc.full502.projectbq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockSearchDto {

    private String name;
    private String manufacturer;
    private String category;
    private Long warehouseId;
}
