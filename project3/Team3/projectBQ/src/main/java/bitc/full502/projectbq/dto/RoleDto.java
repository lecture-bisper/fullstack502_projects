package bitc.full502.projectbq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDto {

    private Long id;
    private String name;
    private char stockIn;
    private char updateMinStock;
    private char addItem;
    private char approveItem;
    private char updateUserInfo;
}
