package bitc.full502.projectbq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MinStockDto {

    private long id;
    private long itemId;
    private String itemName;
    private String itemCode;
    private String itemManufacturer;
    private long itemPrice;
    private long categoryId;
    private String categoryName;
    private String categoryKrName;
    private long stockQuantity;
    private long standardQty;
    private long safetyQty;
    private String minStockStatus;
}
