package bitc.full502.projectbq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockLogDto {

    private long id;
    private String empCode;
    private String empName;
    private LocalDateTime logDate;
    private String type;
    private long quantity;
    private String memo;

    private long warehouseId;
    private String warehouseName;
    private String warehouseKrName;

    private long itemId;
    private String itemCode;
    private String itemName;
    private String itemManufacturer;
    private long itemPrice;

    private long categoryId;
    private String categoryName;
    private String categoryKrName;
}
