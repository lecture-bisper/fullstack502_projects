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
public class StatisticDto<T> {

    private T info;
    private String infoId;
    private long itemId;
    private long totalQuantity;
    private long totalPrice;
    private LocalDateTime latestDate;
}
