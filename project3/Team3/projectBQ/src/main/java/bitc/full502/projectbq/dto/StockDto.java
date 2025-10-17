package bitc.full502.projectbq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDto {

//    Stock 정보 "20250918 완료"

    private Long id;
    private String itemName;
    private String itemCode;
    private String manufacturer;
    private Long quantity;
    private String categoryName;
    private String warehouseName;
    private String warehouseKrName;
}
