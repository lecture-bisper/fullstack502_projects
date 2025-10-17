package bitc.full502.projectbq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogSearchDto {

    private String nameOrCode;
    private String manufacturer;
    private String empCodeOrEmpName;
    private String type;
    private long warehouseId;
    private long categoryId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String keyword;
}
