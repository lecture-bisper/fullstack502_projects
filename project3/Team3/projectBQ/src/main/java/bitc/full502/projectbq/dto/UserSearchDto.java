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
public class UserSearchDto {

    @Builder.Default
    private String nameOrEmpCode = "";

    @Builder.Default
    private String deptCode = "";

    @Builder.Default
    private String roleName = "";

    @Builder.Default
    private LocalDate startDate = LocalDate.of(1900, 1, 1);

    @Builder.Default
    private LocalDate endDate = LocalDate.of(2100, 1, 1);
}
