package bitc.full502.projectbq.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemSearchDto {

//    비품 검색 (비품코드) DTO "20250917 완료"
    private String name;

    private Long categoryId;

    private String code;

    private Long warehouseId;

    private String keyword;

    private String manufacturer;

    private Long minPrice;

    private Long maxPrice;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String status;
}
