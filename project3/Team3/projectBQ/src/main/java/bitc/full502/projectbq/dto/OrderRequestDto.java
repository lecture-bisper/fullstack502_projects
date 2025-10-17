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
public class OrderRequestDto {

    private Long id;
    private Long itemId;
    private String name;
    private String code;
    private String manufacturer;
    private String category;
    private String categoryKrName;
    private Long requestQty;
    private Long price;
    private String requestUser;
    private String requestUserName;
    private String status;
    private LocalDateTime requestDate;
    private String comment;
    private Long standardQty;
    private Long safetyQty;
    private Long stockQuantity;
    private String approveUser;
    private String approveUserName;
}
