package bitc.full502.projectbq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemStockDto {

    private Long id;
    private String code;
    private String name;
    private String manufacturer;
    private Long price;
    private Long categoryId;
    private String categoryName;
    private String categoryKrName;
    private Long stockQuantity;
    private Long standardQty;
    private Long safetyQty;
    private String minStockStatus;
    private String status;
    private String keyword;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
