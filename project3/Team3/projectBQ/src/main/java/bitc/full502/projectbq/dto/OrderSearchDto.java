package bitc.full502.projectbq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchDto {

    private Long categoryId;
    private String status;
    private String keyword;
    private String manufacturer;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
